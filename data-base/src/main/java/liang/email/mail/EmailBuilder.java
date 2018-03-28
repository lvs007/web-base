package liang.email.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于生成一封邮件
 * Created by
 */
public class EmailBuilder {


    private EmailBuilder() {
    }

    public static SimpleEmailBuilder simple(String toUser, String subject, String content) {
        SimpleEmailBuilder builder = new SimpleEmailBuilder();
        builder.toList.add(toUser);
        builder.subject = subject;
        builder.content = content;
        return builder;
    }

    public static class SimpleEmailBuilder {

        private final List<String> toList = new ArrayList<>();
        private final List<String> ccList = new ArrayList<>();
        private final List<String> bccList = new ArrayList<>();
        private String subject;
        private String content;
        private String fromName;

        public SimpleEmailBuilder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public SimpleEmailBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        public SimpleEmailBuilder setFromName(String fromName) {
            this.fromName = fromName;
            return this;
        }

        public SimpleEmailBuilder addTo(String to) {
            toList.add(to);
            return this;
        }

        public SimpleEmailBuilder addTo(List<String> toList) {
            this.toList.addAll(toList);
            return this;
        }

        public SimpleEmailBuilder addCc(String cc) {
            ccList.add(cc);
            return this;
        }

        public SimpleEmailBuilder addCcList(List<String> ccList) {
            this.ccList.addAll(ccList);
            return this;
        }

        public SimpleEmailBuilder addBcc(String bcc) {
            bccList.add(bcc);
            return this;
        }

        public SimpleEmailBuilder addBccList(List<String> bccList) {
            this.bccList.addAll(bccList);
            return this;
        }

        public SimpleEmail build() {
            SimpleEmail email = new SimpleEmail();
            email.setSubject(subject);
            email.setContent(content);
            email.setFromName(fromName);
            email.getToList().addAll(toList);
            email.getCcList().addAll(ccList);
            email.getBccList().addAll(bccList);
            return email;
        }

    }

}



