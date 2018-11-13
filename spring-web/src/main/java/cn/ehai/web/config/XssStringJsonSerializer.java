package cn.ehai.web.config;


import java.io.IOException;

import org.owasp.encoder.Encode;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
 
/**
 * 描述 : 基于xss的JsonSerializer
 *
 * @author wangkang
 *
 */
public class XssStringJsonSerializer extends JsonSerializer<String> {
 
 @Override
 public Class<String> handledType() {
  return String.class;
 }
 
 @Override
 public void serialize(String value, JsonGenerator jsonGenerator,
   SerializerProvider serializerProvider) throws IOException {
	 
  if (value != null) {
   String encodedValue = Encode.forHtmlContent(value);
   jsonGenerator.writeString(encodedValue);
  }
 }
 
}