public enum ERequestType {
    UNHANDLED, CONNECT, MESSAGE, DISCONNECT;

    public static ERequestType getRequestType(String request) {
        String command = request.split(" ")[0];

        switch(command) {
            case "C":
                return ERequestType.CONNECT;
            case "M":
                return ERequestType.MESSAGE;
            case "D":
                return ERequestType.DISCONNECT;
            default:
                return ERequestType.UNHANDLED;
        }
    }
}
