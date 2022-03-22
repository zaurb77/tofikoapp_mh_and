package pojo;

import java.util.ArrayList;

public class RestaurantListPojo {

    public int status;
    public String message;

    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData{

        public int id;
        public String name;
        public String image;
        public String address;
        public String latitude;
        public String longitude;
        public String distance;
        public int is_open;
        public String opens_in;
        public String next_open_time;
        public String delivery_charge;
        public String mobile_no;
        public String days;
        public String open_close_time1;
        public String open_close_time2;
        public String description;
        public double rating;
    }
}
