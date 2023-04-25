package ch.uzh.ifi.hase.soprafs23.webSockets.DTO;

public class UserJoinDTO {
        private final static String type = "playerJoin";
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }
}
