package pojo;

import java.util.ArrayList;

public class GeneralPojo {

    public int status;
    public String message;
    public ResponseData responsedata;

    public class ResponseData {
        public Setting settings;
        public SocialLinks social_links;
        public ArrayList<Banners> banners = new ArrayList<>();
        public String order_status;
        public String cart_id;
        public String currency;
        public String order_type;
        public String restaurant_id;
        public int cart_items;
    }

    public class Setting {
        public String terms;
        public String policy;
        public String cookie;
        public String offer_banner;
        public String youtube_channel;
        public String mangal_link;
        public String facebook_channel;
        public String instagram_channel;
        public String telegram_channel;
        public String facebook_share;
        public String instagram_share;
        public String linkedin_share;
        public String whatsapp_share;
        public String telegram_share;
        public String where_we_are;
        public String about_us;
        public String invite_friends;
        public String menu_search;
        public String grid;
    }

    public class Banners {
        public int id;
        public String image;
        public int ad_type;
        public String item_id;
        public String price;
        public String res_id;
    }

    public class SocialLinks {
        public String fb_url;
        public String insta_url;
        public String mangal_url;
        public String youtube_url;
        public String telegram_url;
        public String insta_img;
        public String referral_msg;
        public String tofiko_url;
    }
}