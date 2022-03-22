package pojo;

import java.util.ArrayList;

public class GetTimePojo {

    public String status;
    public String message;
    public ArrayList<Responsedata> responsedata = new ArrayList<>();

    public class Responsedata{
        public String date;
        public String day;
        public String isOpen;
        public ArrayList<Morning> morning = new ArrayList<>();
        public ArrayList<Evening> evening = new ArrayList<>();
    }

    public class Morning{
        public String time;
        public String discount;
        public String available_capacity;
        public String total_capacity;
    }

    public class Evening{
        public String time;
        public String discount;
        public String available_capacity;
        public String total_capacity;
    }

}
