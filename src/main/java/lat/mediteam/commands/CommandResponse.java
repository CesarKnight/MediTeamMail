package lat.mediteam.commands;

public class CommandResponse {
    private boolean success;
    private String message;
    private String imageBase64;

    public CommandResponse(boolean success, String message, String imageB64){
        this.success = success;
        this.message = message;
        this.imageBase64 = imageB64;
    }

    public CommandResponse(boolean success, String message) {
        this(success, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getImageB64(){
        return imageBase64;
    }
}