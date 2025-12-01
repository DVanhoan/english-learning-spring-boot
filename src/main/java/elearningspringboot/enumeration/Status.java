package elearningspringboot.enumeration;

import elearningspringboot.exception.AppException;

public enum Status {
    ACTIVE("ACTIVE"),
    PENDING("PENDING"),
    BANNED("BANNED");
    private final String name;

    Status(String name){
        this.name = name;
    }
    public static Status getStatusFromName(String name){
        for (Status status : Status.values()){
            if (status.name.equalsIgnoreCase(name))
                return status;
        }
        throw new AppException(ErrorCode.INVALID_STATUS_ENUM);
    }

}
