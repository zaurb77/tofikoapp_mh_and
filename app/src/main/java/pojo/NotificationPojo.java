package pojo;

import java.util.ArrayList;

public class NotificationPojo {

    public String status;
    public String message;
    public ArrayList<Responsedata> responsedata = new ArrayList<>();

    public class Responsedata{
        public String id;
        public String store_name;
        public String heading;
        public String description;
        public String image;
        public String cat;


        public String booking_number;
        public String store_image;
        public String latitude;
        public String longitude;
        public String address;
        public String guests;
        public String status;
        public String dt;
        public String tm;
        public String reason;
        public String notes;
        public String notes_manager;



        public String name;
        public String is_open;
        public String opens_in;
        public String next_open_time;
        public String rating;
        public String delivery_charge;
        public String mobile_no;
        public String days;
        public String open_close_time1;
        public String open_close_time2;
    }
}
