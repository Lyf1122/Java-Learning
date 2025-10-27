package org.generate.mojo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generate sources from DSL JSON.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class CodegenMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  /**
   * DSL source: can be URL or file path. For internal platform, you can set platform token/url here.
   */
  @Parameter(property = "dslSource", defaultValue = "${basedir}/src/main/resources/service-dsl.json")
  private String dslSource;

  @Parameter(property = "outputDir", defaultValue = "${project.build.directory}/generated-sources/codegen")
  private String outputDir;

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void execute() throws MojoExecutionException {
    try {
      getLog().info("Codegen plugin started. DSL source: " + dslSource);
      JsonNode root = loadDsl(dslSource);
      // ensure output dir exists and register with project so maven compiles it
      Path out = Paths.get(outputDir);
      Files.createDirectories(out);

      // read application name and package from project
      String groupId = project.getGroupId();
      String artifactId = project.getArtifactId();
      String basePackage = groupId + "." + artifactId;

      // generate code per API
      JsonNode apis = root.path("apis");
      if (apis.isArray()) {
        for (JsonNode api : apis) {
          generateForApi(api, basePackage, out);
        }
      }

      // register generated-sources so maven compiles them
      project.addCompileSourceRoot(out.toString());
      getLog().info("Codegen plugin finished. Sources generated to: " + out);
    } catch (Exception e) {
      throw new MojoExecutionException("codegen failed", e);
    }
  }

  private JsonNode loadDsl(String source) throws IOException {
    // For demo we treat as local file path. You can extend to support URL with HTTP calls and auth.
    File f = new File(source);
    if (!f.exists()) {
      throw new FileNotFoundException("DSL file not found: " + source);
    }
    return mapper.readTree(f);
  }

  private void generateForApi(JsonNode api, String basePackage, Path out) throws IOException {
    String apiName = api.path("name").asText();
    String apiPath = api.path("path").asText();

    String controllerPkg = basePackage + ".controller";
    String servicePkg = basePackage + ".service";
    String modelPkg = basePackage + ".model";

    // generate controller
    String controllerCode = renderController(apiName, apiPath, controllerPkg, servicePkg, modelPkg, api);
    Path controllerFile = out.resolve(controllerPkg.replace('.', '/')).resolve(apiName + "Controller.java");
    writeFile(controllerFile, controllerCode);

    // generate service interface + impl
    String serviceCode = renderServiceInterface(apiName, servicePkg, modelPkg, api);
    Path serviceFile = out.resolve(servicePkg.replace('.', '/')).resolve(apiName + "Service.java");
    writeFile(serviceFile, serviceCode);

    String serviceImplCode = renderServiceImpl(apiName, servicePkg, modelPkg);
    Path implFile = out.resolve(servicePkg.replace('.', '/')).resolve(apiName + "ServiceImpl.java");
    writeFile(implFile, serviceImplCode);

    // generate model classes for operations
    JsonNode ops = api.path("operations");
    if (ops.isArray()) {
      for (JsonNode op : ops) {
        JsonNode req = op.path("request");
        if (req != null && req.has("name")) {
          String reqCode = renderModelRecord(modelPkg, req);
          Path reqFile = out.resolve(modelPkg.replace('.', '/')).resolve(req.get("name").asText() + ".java");
          writeFile(reqFile, reqCode);
        }
        JsonNode resp = op.path("response");
        if (resp != null && resp.has("name")) {
          String respCode = renderModelRecord(modelPkg, resp);
          Path respFile = out.resolve(modelPkg.replace('.', '/')).resolve(resp.get("name").asText() + ".java");
          writeFile(respFile, respCode);
        }
      }
    }
  }

  private void writeFile(Path target, String content) throws IOException {
    Files.createDirectories(target.getParent());
    try (BufferedWriter w = Files.newBufferedWriter(target)) {
      w.write(content);
    }
  }

  /* ========== TEMPLATES (very simplified) ========== */

  private String renderController(String apiName, String apiPath, String controllerPkg, String servicePkg, String modelPkg, JsonNode api) {
    StringBuilder sb = new StringBuilder();
    sb.append("package ").append(controllerPkg).append(";\n\n");
    sb.append("import org.springframework.web.bind.annotation.*;\n");
    sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
    sb.append("import ").append(servicePkg).append(".").append(apiName).append("Service;\n");
    sb.append("import java.util.*;\n\n");
    sb.append("@RestController\n");
    sb.append("@RequestMapping(\"").append(apiPath).append("\")\n");
    sb.append("public class ").append(apiName).append("Controller {\n\n");
    sb.append("  private final ").append(apiName).append("Service service;\n\n");
    sb.append("  @Autowired\n");
    sb.append("  public ").append(apiName).append("Controller(").append(apiName).append("Service service) {\n");
    sb.append("    this.service = service;\n");
    sb.append("  }\n\n");

    JsonNode ops = api.path("operations");
    if (ops.isArray()) {
      for (JsonNode op : ops) {
        String opName = op.path("name").asText();
        String mapping = op.path("mappingType").asText("POST");
        String path = op.path("path").asText("");
        // request and response handling
        JsonNode req = op.path("request");
        JsonNode params = op.path("requestParams");
        String returnType = "Object";
        JsonNode resp = op.path("response");
        if (resp != null && resp.has("name")) {
          returnType = modelPkg + "." + resp.get("name").asText();
        }

        sb.append("  @").append(mapperFor(mapping)).append("(\"").append(path).append("\")\n");
        sb.append("  public ").append(returnType).append(" ").append(opName).append("(");
        if (req != null && req.has("name")) {
          sb.append("@RequestBody ").append(modelPkg).append(".").append(req.get("name").asText()).append(" body");
        } else if (params.isArray() && params.size() > 0) {
          // simple support for path param first
          boolean first = true;
          for (JsonNode p : params) {
            if (!first) sb.append(", ");
            String pname = p.path("name").asText();
            String ptype = mapType(p.path("type").asText("string"));
            String in = p.path("in").asText("query");
            if ("path".equals(in)) {
              sb.append("@PathVariable ").append(ptype).append(" ").append(pname);
            } else {
              sb.append("@RequestParam ").append(ptype).append(" ").append(pname);
            }
            first = false;
          }
        }
        sb.append(") {\n");
        sb.append("    return service.").append(opName).append("(");
        // pass args
        if (req != null && req.has("name")) {
          sb.append("body");
        } else if (params.isArray() && params.size() > 0) {
          boolean first = true;
          for (JsonNode p : params) {
            if (!first) sb.append(", ");
            sb.append(p.path("name").asText());
            first = false;
          }
        }
        sb.append(");\n  }\n\n");
      }
    }

    sb.append("}\n");
    return sb.toString();
  }

  private String renderServiceInterface(String apiName, String servicePkg, String modelPkg, JsonNode api) {
    StringBuilder sb = new StringBuilder();
    sb.append("package ").append(servicePkg).append(";\n\n");
    sb.append("import ").append(modelPkg).append(".*;\n");
    sb.append("public interface ").append(apiName).append("Service {\n");
    JsonNode ops = api.path("operations");
    if (ops.isArray()) {
      for (JsonNode op : ops) {
        String opName = op.path("name").asText();
        String returnType = "Object";
        JsonNode resp = op.path("response");
        if (resp != null && resp.has("name"))
          returnType = modelPkg + "." + resp.get("name").asText();
        sb.append("  ").append(returnType).append(" ").append(opName).append("(");
        JsonNode req = op.path("request");
        JsonNode params = op.path("requestParams");
        if (req != null && req.has("name")) {
          sb.append(modelPkg).append(".").append(req.get("name").asText()).append(" request");
        } else if (params.isArray() && params.size() > 0) {
          boolean first = true;
          for (JsonNode p : params) {
            if (!first) sb.append(", ");
            sb.append(mapType(p.path("type").asText("string"))).append(" ").append(p.path("name").asText());
            first = false;
          }
        }
        sb.append(");\n");
      }
    }
    sb.append("}\n");
    return sb.toString();
  }

  private String renderServiceImpl(String apiName, String servicePkg, String modelPkg) {
    return "package " + servicePkg + ";\n\n" +
      "import org.springframework.stereotype.Service;\n" +
      "\n@Service\n" +
      "public class " + apiName + "ServiceImpl implements " + apiName + "Service {\n\n" +
      "  // TODO: inject mappers/repositories here\n\n" +
      "  public " + apiName + "ServiceImpl() {}\n\n" +
      "  // auto-generated methods to be implemented by developer\n}\n";
  }

  private String renderModelRecord(String modelPkg, JsonNode typeNode) {
    String name = typeNode.get("name").asText();
    JsonNode props = typeNode.path("properties");
    StringBuilder sb = new StringBuilder();
    sb.append("package ").append(modelPkg).append(";\n\n");
    sb.append("import com.fasterxml.jackson.annotation.JsonProperty;\n");
    sb.append("import java.time.OffsetDateTime;\n\n");
    sb.append("public record ").append(name).append("(\n");
    if (props != null && props.fieldNames() != null) {
      boolean first = true;
      var it = props.fieldNames();
      while (it.hasNext()) {
        String prop = it.next();
        String t = mapType(props.path(prop).path("type").asText("string"));
        if (!first) sb.append(",\n");
        sb.append("    @JsonProperty(\"").append(prop).append("\") ").append(t).append(" ").append(prop);
        first = false;
      }
    }
    sb.append(") {\n\n");
    // builder
    sb.append("  public static Builder builder() { return new Builder(); }\n\n");
    sb.append("  public static class Builder {\n");
    if (props != null && props.fieldNames() != null) {
      var it2 = props.fieldNames();
      while (it2.hasNext()) {
        String prop = it2.next();
        String t = mapType(props.path(prop).path("type").asText("string"));
        sb.append("    private ").append(t).append(" ").append(prop).append(";\n");
        sb.append("    public Builder ").append(prop).append("(").append(t).append(" ").append(prop).append("){ this.").append(prop).append("=").append(prop).append("; return this; }\n");
      }
      // build method
      sb.append("    public ").append(name).append(" build(){ return new ").append(name).append("(");
      var it3 = props.fieldNames();
      boolean first2 = true;
      while (it3.hasNext()) {
        if (!first2) sb.append(", ");
        sb.append(it3.next());
        first2 = false;
      }
      sb.append("); }\n");
    }
    sb.append("  }\n");
    sb.append("}\n");
    return sb.toString();
  }

  private String mapperFor(String mapping) {
    mapping = mapping == null ? "POST" : mapping.toUpperCase();
    return switch (mapping) {
      case "GET" -> "GetMapping";
      case "PUT" -> "PutMapping";
      case "DELETE" -> "DeleteMapping";
      case "PATCH" -> "PatchMapping";
      default -> "PostMapping";
    };
  }

  private String mapType(String t) {
    return switch (t) {
      case "long", "integer", "int" -> "Long";
      case "number", "double", "float" -> "Double";
      case "boolean" -> "Boolean";
      case "string" -> "String";
      case "date-time" -> "java.time.OffsetDateTime";
      default -> "String";
    };
  }
}
