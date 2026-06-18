package lat.mediteam.mail;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents an email message parsed from a raw POP3 RETR response,
 * or built from scratch to be sent.
 *
 * Supports:
 *  - Simple plain-text emails (e.g. sent via Telnet/SMTP)
 *  - Multipart MIME emails (e.g. sent from Gmail)
 *  - Building a multipart MIME body (plain text + 1 base64 image) for sending,
 *    retrievable raw via {@link #getBody()}
 */
@Getter
@Setter
public class Email {

    private String mailServer;
    private String sender;
    private String recipient;
    private String subject;
    private String body;

    /** Boundary used when building a multipart MIME body (plain text + image). */
    private String mimeBoundary;

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

    public Email() {
        this.mimeBoundary = "----=_Boundary_" + UUID.randomUUID().toString().replace("-", "");
    }

    // -------------------------------------------------------------------------
    // Building outbound body (plain text + image)
    // -------------------------------------------------------------------------

    /**
     * Initializes the multipart MIME body with the outer headers and the
     * plain-text part. Must be called before {@link #addImageB64(String)}.
     *
     * If called with a null/blank text, an empty plain-text part is still
     * written so the MIME structure stays valid.
     *
     * @param plainTextBody The plain-text content of the email
     */
    public void addPlainBody(String plainTextBody) {
        String text = plainTextBody != null ? plainTextBody : "";

        StringBuilder sb = new StringBuilder();
        sb.append("MIME-Version: 1.0\r\n");
        sb.append("Content-Type: multipart/mixed; boundary=\"").append(mimeBoundary).append("\"\r\n");
        sb.append("\r\n");
        sb.append("--").append(mimeBoundary).append("\r\n");
        sb.append("Content-Type: text/plain; charset=\"UTF-8\"\r\n");
        sb.append("\r\n");
        sb.append(text).append("\r\n");

        this.body = sb.toString();
    }

    /**
     * Appends a single base64-encoded image as a MIME part to the body
     * started by {@link #addPlainBody(String)}, and closes the MIME structure.
     *
     * If imageB64 is null/blank, no image part is added and the body is
     * simply closed after the plain-text part.
     *
     * @param imageB64 Raw base64-encoded image data (already encoded, no headers)
     */
    public void addImageB64(String imageB64) {
        StringBuilder sb = new StringBuilder(this.body);

        if (imageB64 != null && !imageB64.isBlank()) {
            sb.append("--").append(mimeBoundary).append("\r\n");
            sb.append("Content-Type: image/png; name=\"image.png\"\r\n");
            sb.append("Content-Transfer-Encoding: base64\r\n");
            sb.append("Content-Disposition: attachment; filename=\"image.png\"\r\n");
            sb.append("\r\n");
            sb.append(wrapBase64(imageB64)).append("\r\n");
        }

        sb.append("--").append(mimeBoundary).append("--\r\n");

        this.body = sb.toString();
    }

    /**
     * Wraps a base64 string at 76 characters per line with CRLF, as required
     * by RFC 2045 and expected by major email providers like Gmail.
     *
     * @param base64 Unwrapped base64 string
     * @return The base64 string wrapped into 76-char lines
     */
    private String wrapBase64(String base64) {
        StringBuilder wrapped = new StringBuilder();
        int lineLength = 76;
        for (int i = 0; i < base64.length(); i += lineLength) {
            wrapped.append(base64, i, Math.min(i + lineLength, base64.length()));
            wrapped.append("\r\n");
        }
        return wrapped.toString().stripTrailing();
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