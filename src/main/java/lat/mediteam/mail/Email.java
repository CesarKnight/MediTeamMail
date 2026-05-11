package lat.mediteam.mail;

public class Email {
    String server;
    String sender;
    String recipient;
    String subject;
    String body;

    public Email(String raw) {
        this.server = "";
        this.sender = "";
        this.recipient = "";
        this.subject = "";
        this.body = "";

        if (raw == null || raw.isEmpty()) {
            return;
        }

        String normalized = raw.replace("\r\n", "\n");
        String[] parts = normalized.split("\n\n", 2);
        String headers = parts[0].replaceAll("\n[ \t]+", " ");
        String content = parts.length > 1 ? parts[1] : "";

        for (String headerLine : headers.split("\n")) {
            String lower = headerLine.toLowerCase();

            if (lower.startsWith("from:")) {
                this.sender = normalizeAddress(headerLine.substring(5).trim());
            } else if (lower.startsWith("to:") && this.recipient.isEmpty()) {
                this.recipient = normalizeAddress(headerLine.substring(3).trim());
            } else if (lower.startsWith("received:")) {
                if (this.server.isEmpty()) {
                    this.server = extractServerFromReceived(headerLine);
                }

                String receivedRecipient = extractRecipientFromReceived(headerLine);
                if (!receivedRecipient.isEmpty()) {
                    this.recipient = normalizeAddress(receivedRecipient);
                }
            } else if (lower.startsWith("subject:")) {
                this.subject = headerLine.substring(8).trim();
            }
        }

        this.body = extractBody(content);
    }

    public String getServer() {
        return server;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    private String extractBody(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        String boundary = findBoundary(content);

        if (boundary.isEmpty()) {
            return stripPartHeaders(content).trim();
        }

        String plainText = "";
        String htmlText = "";

        String[] sections = content.split("--" + java.util.regex.Pattern.quote(boundary));
        for (String section : sections) {
            String trimmed = section.trim();
            if (trimmed.isEmpty() || trimmed.equals("--")) {
                continue;
            }

            String sectionLower = trimmed.toLowerCase();
            if (sectionLower.contains("content-type: text/plain")) {
                plainText = stripPartHeaders(trimmed).trim();
            } else if (sectionLower.contains("content-type: text/html")) {
                htmlText = stripPartHeaders(trimmed).trim();
            }
        }

        return !plainText.isEmpty() ? plainText : htmlText;
    }

    private String findBoundary(String content) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            String lower = line.toLowerCase();
            if (lower.startsWith("content-type:") && lower.contains("boundary=")) {
                int idx = lower.indexOf("boundary=");
                String value = line.substring(idx + "boundary=".length()).trim();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                return value;
            }

            // The first boundary marker in body can also reveal the boundary name.
            if (line.startsWith("--") && line.length() > 2) {
                return line.substring(2).trim();
            }
        }
        return "";
    }

    private String stripPartHeaders(String part) {
        String normalized = part.replace("\r\n", "\n");
        String[] split = normalized.split("\n\n", 2);
        return split.length > 1 ? split[1] : split[0];
    }

    private String extractServerFromReceived(String receivedHeader) {
        String trimmed = receivedHeader.substring("Received:".length()).trim();

        int fromIndex = trimmed.toLowerCase().indexOf("from ");
        if (fromIndex >= 0) {
            String afterFrom = trimmed.substring(fromIndex + 5).trim();
            int firstSpace = afterFrom.indexOf(' ');
            if (firstSpace > 0) {
                return afterFrom.substring(0, firstSpace).trim();
            }
            return afterFrom;
        }

        return trimmed;
    }

    private String extractRecipientFromReceived(String receivedHeader) {
        String trimmed = receivedHeader.substring("Received:".length()).trim();
        String lower = trimmed.toLowerCase();

        int forIndex = lower.indexOf(" for ");
        if (forIndex < 0) {
            return "";
        }

        String afterFor = trimmed.substring(forIndex + 5).trim();
        if (afterFor.isEmpty()) {
            return "";
        }

        if (afterFor.startsWith("<")) {
            int endBracket = afterFor.indexOf('>');
            if (endBracket > 1) {
                return afterFor.substring(1, endBracket).trim();
            }
        }

        int endSemicolon = afterFor.indexOf(';');
        int endSpace = afterFor.indexOf(' ');

        int end = -1;
        if (endSemicolon >= 0 && endSpace >= 0) {
            end = Math.min(endSemicolon, endSpace);
        } else if (endSemicolon >= 0) {
            end = endSemicolon;
        } else if (endSpace >= 0) {
            end = endSpace;
        }

        if (end > 0) {
            return afterFor.substring(0, end).trim();
        }

        return afterFor;
    }

    private String normalizeAddress(String value) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        int start = trimmed.indexOf('<');
        int end = trimmed.indexOf('>');

        if (start >= 0 && end > start) {
            return trimmed.substring(start + 1, end).trim();
        }

        return trimmed.replace("<", "").replace(">", "").trim();
    }
}
