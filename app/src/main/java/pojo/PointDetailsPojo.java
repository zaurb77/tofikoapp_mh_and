package pojo;

public class PointDetailsPojo {

    public int status;
    public String message;

    public ResponseData responsedata;


    public class ResponseData {

        public String total_points;
        public String fb_share_point;
        public String insta_share_point;
        public String order_point;

        public  ReferralPoint referral_point;

        public class ReferralPoint {
            public String you_got;
            public String friend_got;

        }
    }
}

