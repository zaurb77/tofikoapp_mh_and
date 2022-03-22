package pojo;

import java.util.ArrayList;

public class StoreDetailPojo {

    public String status;
    public String message;
    public  Responsedata responsedata;

    public class Responsedata{
        public String id;
        public String name;
        public String description;
        public String image;
        public String address;
        public String latitude;
        public String longitude;
        public String is_open;
        public String opens_in;
        public String next_open_time;
        public String rating;
        public String delivery_charge;
        public String mobile_no;

        public ArrayList<Review> reviews = new ArrayList<>();
        public ArrayList<OpenCloseTime> open_close_time = new ArrayList<>();

        public String member_capacity;
    }

    public class Review{
        public String id;
        public String user_name;
        public String rating;
        public String review_text;
        public String reply_text;
        public String added_date;
    }

    public class OpenCloseTime{
        public String day;
        public String open_time;
        public String open_time1;
        public String close_time;
        public String close_time1;
        public String isopen;
    }
}
