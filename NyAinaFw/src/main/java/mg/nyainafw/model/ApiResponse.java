package mg.nyainafw.model;

public class ApiResponse {
    private Object data;

    public ApiResponse() {
    }

    public ApiResponse(Object data) {
        this.data = data;
    }

    public static ApiResponse ok(Object data) {
        return new ApiResponse(data);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
