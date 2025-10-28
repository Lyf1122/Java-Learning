package com.lyf.lib.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.apache.commons.lang3.builder.ToStringStyle.SIMPLE_STYLE;

@JsonPropertyOrder({"requestId", "code", "subject"})
@JsonInclude(NON_NULL)
public record ErrorResponseDto(
    @JsonProperty("code") String errorCode,
    @JsonProperty("requestId") String requestId,
    @JsonProperty("subject") String message) {

  @Override
  public String toString() {
    return new ToStringBuilder(this, SIMPLE_STYLE)
      .append("code", errorCode)
      .append("requestId", requestId)
      .append("subject", message)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ErrorResponseDto that = (ErrorResponseDto) o;

    return new EqualsBuilder().append(message, that.message)
        .append(requestId, that.requestId).append(errorCode, that.errorCode).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(message).append(requestId).append(errorCode)
        .toHashCode();
  }
}
