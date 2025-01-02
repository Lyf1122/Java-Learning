package org.example.patterns.prototype;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@JsonPropertyOrder({ "logo", "title", "sections" })
@JsonInclude(NON_EMPTY)
public record ScreenForm(
    @JsonProperty("logo") String logo,
    @JsonProperty("title") String title,
    @JsonProperty("sections") ScreenFormSection[] sections
) {

  public static ScreenFormBuilder of() {
    return new ScreenFormBuilder();
  }

  public static ScreenFormSectionBuilder ofSection() {
    return new ScreenFormSectionBuilder();
  }

  public static ScreenComponentBuilder ofComponent() {
    return new ScreenComponentBuilder();
  }

  @JsonPropertyOrder({ "h1", "h2", "components", "f1", "f2" })
  @JsonInclude(NON_EMPTY)
  public record ScreenFormSection(

      @JsonProperty("h1") String h1,
      @JsonProperty("h2") String h2,
      @JsonProperty("components") ScreenFormComponent[] components,
      @JsonProperty("f1") String f1,
      @JsonProperty("f2") String f2

  ) {}

  @JsonPropertyOrder({ "code", "mode", "multiple" })
  @JsonInclude(NON_EMPTY)
  public record ScreenFormComponent(

      @JsonProperty("code") String code,
      @JsonProperty("version") String version,
      @JsonProperty("editable") boolean editable,
      @JsonProperty("multiple") boolean multiple

  ) {}

  public static class ScreenFormBuilder {

    private String logo;
    private String title;
    private final List<ScreenFormSection> sectionList = new LinkedList<>();

    public ScreenForm build() {
      return new ScreenForm(logo, title, sectionList.toArray(ScreenFormSection[]::new));
    }

    public ScreenFormBuilder copy() {
      return ScreenForm.of().logo(logo).title(title).addSections(sectionList.toArray(ScreenFormSection[]::new));
    }

    public ScreenFormBuilder logo(String logo) {
      this.logo = logo;
      return this;
    }

    public ScreenFormBuilder title(String title) {
      this.title = title;
      return this;
    }

    public ScreenFormBuilder addSection(ScreenFormSection step) {
      Optional.ofNullable(step).ifPresent(sectionList::add);
      return this;
    }

    public ScreenFormBuilder addSections(ScreenFormSection[] sections) {
      if (sections != null) {
        sectionList.addAll(Arrays.asList(sections));
      }
      return this;
    }
  }

  public static class ScreenFormSectionBuilder {

    private String h1;
    private String h2;
    private String f1;
    private String f2;

    private final List<ScreenFormComponent> componentList = new LinkedList<>();

    public ScreenFormSection build() {
      return new ScreenFormSection(h1, h2, componentList.toArray(new ScreenFormComponent[0]), f1, f2);
    }

    public ScreenFormSectionBuilder h1(String h1) {
      this.h1 = h1;
      return this;
    }

    public ScreenFormSectionBuilder h2(String h2) {
      this.h2 = h2;
      return this;
    }

    public ScreenFormSectionBuilder f1(String f1) {
      this.f1 = f1;
      return this;
    }

    public ScreenFormSectionBuilder f2(String f2) {
      this.f2 = f2;
      return this;
    }

    public ScreenFormSectionBuilder addComponent(ScreenFormComponent comp) {
      Optional.ofNullable(comp).ifPresent(componentList::add);
      return this;
    }

  }

  public static class ScreenComponentBuilder {

    private String code;
    private String version;
    private boolean editable;
    private boolean multiple;

    public ScreenFormComponent build() {
      return new ScreenFormComponent(code, version, editable, multiple);
    }

    public ScreenComponentBuilder code(String code) {
      this.code = code;
      return this;
    }

    public ScreenComponentBuilder version(String version) {
      this.version = version;
      return this;
    }

    public ScreenComponentBuilder editable(boolean editable) {
      this.editable = editable;
      return this;
    }

    public ScreenComponentBuilder multiple(boolean multiple) {
      this.multiple = multiple;
      return this;
    }
  }

}

