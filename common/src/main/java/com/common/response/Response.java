package com.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Accessors(chain = true)
@Getter
@Setter
@ApiModel(value = "Response")
public class Response<T> {
    @JsonIgnore
    static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    @JsonProperty
    @ApiModelProperty(required = true,
            notes = "Trace Id to uniquely Identify each request")
    String traceId = null;

    @JsonProperty
    @ApiModelProperty(required = true,
            notes = "Timestamp of the request")
    String timeStamp = null;

    @JsonProperty
    @ApiModelProperty(required = true,
            notes = "Status cod eof the Response",
            allowableValues = "200,400,401,405,500")
    int status = 500;

    @JsonProperty
    @ApiModelProperty(required = true,
            notes = "Response Data")
    T message = null;

    @JsonProperty
    @ApiModelProperty(required = true,
            notes = "Response error message")
    String error = null;

    @JsonProperty
    @ApiModelProperty(required = true,
            notes = "API path hit by the request for tracing")
    String path;

    public Response() {
        this.timeStamp = Instant.now().toString();
        this.path = this.computePath();
        this.traceId = this.computeTraceId();
    }

    private String computeTraceId() {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            try {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                if (requestAttributes instanceof ServletRequestAttributes) {
                    HttpServletRequest request = (HttpServletRequest) ((ServletRequestAttributes) requestAttributes).getRequest();
                    traceId = request.getHeader("messageId");
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
            if (traceId == null) {
                traceId = "INTERNALGEN-" + UUID.randomUUID().toString();
            }
        }
        return traceId;
    }

    private String computePath() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request = (HttpServletRequest) ((ServletRequestAttributes) requestAttributes).getRequest();
                return request.getRequestURI();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;

    }

    @Override
    public String toString() {
        try {
            return mapper.writeValueAsString((this));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return super.toString();
    }

    public ResponseEntity<Response<T>> toResponseEntity() {
        return new ResponseEntity<>(this, HttpStatus.valueOf(this.status));
    }
}
