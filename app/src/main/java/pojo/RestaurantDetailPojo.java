package pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class RestaurantDetailPojo {


    public int status;
    public String message;
    public ResponseData responsedata;

    public class ResponseData {

        public int id;
        public String name;
        public String address;
        public String latitude;
        public String longitude;
        public String zipcode;
        public String rating;
        public String mobile_no;
        public String without_break;

        public ArrayList<Images> images = new ArrayList<>();
        public ArrayList<OpenCloseTime> open_close_time = new ArrayList<>();

        public class Images {
            public int id;
            public String image;
        }

        public class OpenCloseTime implements Serializable {
            public String open_time;
            public String open_time1;
            public String close_time;
            public String close_time1;
            public String isopen;
        }
    }
}
