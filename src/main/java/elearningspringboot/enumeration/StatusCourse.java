package elearningspringboot.enumeration;

public enum StatusCourse {
    PUBLIC("PUBLIC"),
    HIDDEN("HIDDEN");

    private final String value;

    StatusCourse(String value){
        this.value = value;
    }

    public static StatusCourse fromValue(String value){
        for(StatusCourse statusCourse : StatusCourse.values()){
            if(statusCourse.value.equals(value)){
                return statusCourse;
            }
        }
        throw new IllegalArgumentException("Invalid StatusCourse value: " + value);
    }
}
