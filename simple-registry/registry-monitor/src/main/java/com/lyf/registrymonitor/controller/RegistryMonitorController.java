package com.lyf.registrymonitor.controller;

import com.lyf.lib.exception.InvalidClientRequestException;
import com.lyf.registrymonitor.service.RegistryMonitorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Validated
@RequestMapping(RegistryMonitorController.URI)
public class RegistryMonitorController {
  final static String URI = "/api/v1";

  private final RegistryMonitorService service;

  public RegistryMonitorController(RegistryMonitorService service) {
    this.service = service;
  }

  @PostMapping(path = "/disconnect")
  public ResponseEntity<String> disconnect() {
    service.disconnect();
    return ResponseEntity.status(200).body("OK");
  }

  @PostMapping(path = "/connect")
  public ResponseEntity<String> connect() {
    service.connect();
    return ResponseEntity.status(200).body("OK");
  }

  @PostMapping(path = "/register")
  public ResponseEntity<String> register(
    @RequestParam(value = "namespace") String namespace, @RequestParam(value = "service") String serviceName,
    @RequestParam(value = "protocol") String protocol, @RequestParam(value = "url") String url, @RequestParam(value = "ids") String ids) {

    service.register(namespace, serviceName, protocol, url, ids);
    return ResponseEntity.status(200).body("OK");
  }

  @PostMapping(path = "/subscribe")
  public ResponseEntity<String> subscribe(
    @RequestParam(value = "namespace", required = false) String namespace,
    @RequestParam(value = "msgType", required = false) String msgType,
    @RequestParam(value = "cfgTopic", required = false) String cfgTopic
  ) {

    if(StringUtils.isAnyBlank(namespace, msgType) && StringUtils.isBlank(cfgTopic)) {
      throw new InvalidClientRequestException("Invalid request data");
    }

    if(StringUtils.isNotBlank(cfgTopic)) service.subscribe(cfgTopic);
    else service.subscribe(namespace, msgType);
    return ResponseEntity.status(200).body("OK");
  }

  @PostMapping(path = "/unsubscribe")
  public ResponseEntity<String> unsubscribe(
    @RequestParam(value = "namespace", required = false) String namespace,
    @RequestParam(value = "msgType", required = false) String msgType,
    @RequestParam(value = "cfgTopic", required = false) String cfgTopic
  ) {

    if(StringUtils.isAnyBlank(namespace, msgType) && StringUtils.isBlank(cfgTopic)) {
      throw new InvalidClientRequestException("Invalid request data");
    }

    if(StringUtils.isNotBlank(cfgTopic)) service.unsubscribe(cfgTopic);
    else service.unsubscribe(namespace, msgType);
    return ResponseEntity.status(200).body("OK");
  }

  @PostMapping(path = "/info")
  public ResponseEntity<String> info() {
    return ResponseEntity.status(200).body(service.info());
  }

  @GetMapping(path = "/info/{sid}")
  public ResponseEntity<String> info(@PathVariable(value = "sid") String sid) {
    return ResponseEntity.status(200).body(service.getInfo(sid));
  }

  @GetMapping(path = "/info")
  public ResponseEntity<Map<String, String>> getAllInfo() {
    return ResponseEntity.status(200).body(service.getInfo());
  }


}
