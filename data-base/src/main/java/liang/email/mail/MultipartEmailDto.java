package liang.email.mail;

import liang.common.util.Encodes;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by
 */
public class MultipartEmailDto extends SimpleEmail{
    private final Map<String, String> attachments = new LinkedHashMap<>();

    public Map<String, String> getAttachments() {
        return attachments;
    }


    public static MultipartEmailDto convertFromMultipartEmail(MultipartEmail email) throws IOException {

        MultipartEmailDto dto = new MultipartEmailDto();
        dto.setContent(email.getContent());
        dto.setSubject(email.getSubject());
        dto.setFromName(email.getFromName());
        {
            for(String to : email.getToList()){
                dto.addTo(to);
            }
            for(String cc : email.getCcList()){
                dto.addCc(cc);
            }
            for(String bcc : email.getBccList()){
                dto.addBcc(bcc);
            }
        }

        {
            for(Map.Entry<String,InputStreamSource> entry : email.getAttachments().entrySet()){
                dto.getAttachments().put(entry.getKey(),
                        Encodes.encodeBase64(IOUtils.toByteArray(entry.getValue().getInputStream())));
            }

        }
        return dto;

    }

    public static MultipartEmail convertFromDto(MultipartEmailDto dto){

        MultipartEmail email = new MultipartEmail();
        email.setContent(dto.getContent());
        email.setSubject(dto.getSubject());
        email.setFromName(dto.getFromName());
        {
            for(String to : dto.getToList()){
                email.addTo(to);
            }
            for(String cc : dto.getCcList()){
                email.addCc(cc);
            }
            for(String bcc : dto.getBccList()){
                email.addBcc(bcc);
            }
        }
        {
            for(final Map.Entry<String,String> entry : dto.getAttachments().entrySet()){
                email.getAttachments().put(entry.getKey(),
                        new InputStreamSource() {
                            @Override
                            public InputStream getInputStream() throws IOException {
                                return new ByteArrayInputStream(Encodes.decodeBase64(entry.getValue()));
                            }
                        });
            }

        }
        return email;
    }
}
