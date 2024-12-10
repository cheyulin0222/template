package com.arplanet.template.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "回應模型")
public class ResponseModel<T> {

    public static final String SUCCESS_CODE = "0000";

    @Schema(description = "回應碼")
    private String code;

    @Schema(description = "回應訊息")
    private String message;

    @Schema(description = "回應資料")
    private T data;

    @Schema(description = "請求是否成功")
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(this.getCode());
    }

    public ResponseModel(T data) {
        this.data = data;
        this.code = SUCCESS_CODE;
    }
}
