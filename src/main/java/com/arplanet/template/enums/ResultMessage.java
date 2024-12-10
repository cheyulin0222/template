package com.arplanet.template.enums;

import lombok.Getter;

@Getter
public enum ResultMessage {

    INSERT_SUCCESSFUL("新增成功"),
    INSERT_FAILED("新增失敗"),
    UPDATE_SUCCESSFUL("修改成功"),
    DELETE_SUCCESSFUL("刪除成功"),
    DELETE_FAILED("刪除失敗"),
    SAVE_SUCCESSFUL("儲存成功"),
    IMPORT_SUCCESSFUL("匯入成功"),
    IMPORT_FAILED("匯入失敗");

    private final String message;

    private ResultMessage(String message) {
        this.message = message;
    }
}
