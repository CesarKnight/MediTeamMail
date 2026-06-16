package lat.mediteam.mail;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an email message parsed from a raw POP3 RETR response.
 *
 * Supports:
 *  - Simple plain-text emails (e.g. sent via Telnet/SMTP)
 *  - Multipart MIME emails (e.g. sent from Gmail)
 */
@Getter
@Setter
public class Email {

    private String mailServer;
    private String sender;
    private String recipient;
    private String subject;
    private String body;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Parses a raw POP3 RETR response string into an Email object.
     *
     * @param raw        The full string returned by Pop3Client.readEmail()
     * @param mailServer The mail server host this email was retrieved from
     */
    public Email(String raw, String mailServer) {
        this.mailServer = mailServer;
        parse(raw);
    }

    // -------------------------------------------------------------------------
    // Parsing orchestration
    // -------------------------------------------------------------------------

    /**
     * Entry point for parsing. Splits the raw response into a header block
     * and a body block, then delegates each to its own parser.
     */
    private void parse(String raw) {
        String cleaned = stripPop3StatusLine(raw);

        // Headers and body are separated by the first blank line
        int headerBodySplit = cleaned.indexOf("\r\n\r\n");
        if (headerBodySplit == -1) headerBodySplit = cleaned.indexOf("\n\n");

        if (headerBodySplit == -1) {
            // Malformed email — treat entire content as body
            parseHeaders("");
            this.body = cleaned.trim();
            return;
        }

        String headerBlock = cleaned.substring(0, headerBodySplit);
        String bodyBlock   = cleaned.substring(headerBodySplit).trim();

        parseHeaders(headerBlock);
        this.body = parseBody(bodyBlock, headerBlock);
    }

    // -------------------------------------------------------------------------
    // Header parsing
    // -------------------------------------------------------------------------

    /**
     * Extracts From, To, and Subject from the header block.
     * Handles folded headers (continuation lines starting with whitespace).
     */
    private void parseHeaders(String headerBlock) {
        // Unfold folded header lines (RFC 2822: CRLF followed by whitespace)
        String unfolded = headerBlock
                .replaceAll("\r\n[ \t]+", " ")
                .replaceAll("\n[ \t]+", " ");

        for (String line : unfolded.split("\r?\n")) {
            String lower = line.toLowerCase();
            if (lower.startsWith("from:")) {
                this.sender = extractAddress(line.substring(5).trim());
            } else if (lower.startsWith("to:")) {
                this.recipient = extractAddress(line.substring(3).trim());
            } else if (lower.startsWith("delivered-to:")) {
                this.recipient = extractAddress(line.substring(3).trim());
            } else if (lower.startsWith("subject:")) {
                this.subject = line.substring(8).trim();
            }
        }
    }

    /**
     * Extracts a clean email address from a header value that may be in
     * "Display Name <email@example.com>" or plain "<email@example.com>" format.
     *
     * @param value Raw header value after the colon
     * @return The bare email address, or the original value if no angle brackets found
     */
    private String extractAddress(String value) {
        int start = value.indexOf('<');
        int end   = value.indexOf('>');
        if (start != -1 && end != -1 && end > start) {
            return value.substring(start + 1, end).trim();
        }
        return value.trim();
    }

    // -------------------------------------------------------------------------
    // Body parsing
    // -------------------------------------------------------------------------

    /**
     * Determines whether the email is multipart MIME or plain text and
     * delegates to the appropriate parser.
     *
     * @param bodyBlock   Everything after the blank line separating headers/body
     * @param headerBlock The raw header block, used to detect Content-Type
     * @return The parsed, human-readable body text
     */
    private String parseBody(String bodyBlock, String headerBlock) {
        String boundary = extractMimeBoundary(headerBlock);
        if (boundary != null) {
            return parseMimeBody(bodyBlock, boundary);
        }
        return bodyBlock.trim();
    }

    /**
     * Looks for a MIME boundary declaration in the Content-Type header.
     * Example: {@code Content-Type: multipart/alternative; boundary="abc123"}
     *
     * @return The boundary string (without leading "--"), or null if not multipart
     */
    private String extractMimeBoundary(String headerBlock) {
        String unfolded = headerBlock
                .replaceAll("\r\n[ \t]+", " ")
                .replaceAll("\n[ \t]+", " ");

        for (String line : unfolded.split("\r?\n")) {
            if (line.toLowerCase().startsWith("content-type:") && line.contains("boundary=")) {
                int idx = line.indexOf("boundary=");
                String boundary = line.substring(idx + 9).trim();
                // Strip surrounding quotes if present
                if (boundary.startsWith("\"")) {
                    boundary = boundary.substring(1, boundary.lastIndexOf("\""));
                }
                return boundary;
            }
        }
        return null;
    }

    /**
     * Parses a multipart MIME body and returns the content of the
     * {@code text/plain} part. Falls back to {@code text/html} (stripped)
     * if no plain-text part is found.
     *
     * @param bodyBlock The raw body block
     * @param boundary  The MIME boundary string (without "--" prefix)
     * @return The plain-text content of the best matching MIME part
     */
    private String parseMimeBody(String bodyBlock, String boundary) {
        String delimiter = "--" + boundary;
        String[] parts = bodyBlock.split("(?m)^" + java.util.regex.Pattern.quote(delimiter));

        String htmlFallback = null;

        for (String part : parts) {
            if (part.isBlank() || part.trim().equals("--")) continue;

            // Each part has its own mini header block and body
            int partSplit = part.indexOf("\r\n\r\n");
            if (partSplit == -1) partSplit = part.indexOf("\n\n");
            if (partSplit == -1) continue;

            String partHeaders = part.substring(0, partSplit).toLowerCase();
            String partBody    = part.substring(partSplit).trim();

            if (partHeaders.contains("text/plain")) {
                return partBody;
            }
            if (partHeaders.contains("text/html")) {
                htmlFallback = stripHtmlTags(partBody);
            }
        }

        return htmlFallback != null ? htmlFallback : bodyBlock.trim();
    }

    /**
     * Removes HTML tags from a string for use as a plain-text fallback.
     *
     * @param html Raw HTML string
     * @return Plain text with tags removed and whitespace normalized
     */
    private String stripHtmlTags(String html) {
        return html.replaceAll("<[^>]+>", "").trim();
    }

    // -------------------------------------------------------------------------
    // POP3 response cleanup
    // -------------------------------------------------------------------------

    /**
     * Removes the first "+OK ... octets" line that POP3 prepends to RETR responses.
     *
     * @param raw The raw POP3 response
     * @return The email content without the status line
     */
    private String stripPop3StatusLine(String raw) {
        int firstNewline = raw.indexOf('\n');
        if (firstNewline != -1 && raw.startsWith("+OK")) {
            return raw.substring(firstNewline + 1);
        }
        return raw;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Email{" +
                "mailServer='" + mailServer + '\'' +
                ", sender='"    + sender    + '\'' +
                ", recipient='" + recipient + '\'' +
                ", subject='"   + subject   + '\'' +
                ", body='"      + body      + '\'' +
                '}';
    }
}