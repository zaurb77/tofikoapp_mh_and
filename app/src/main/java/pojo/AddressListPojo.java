package pojo;

import java.util.ArrayList;

public class AddressListPojo {

    public int status;
    public String message;

    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData {
        public int address_id;
        public String address_type;
        public String address_line;
        public String address;
        public String zip_code;
        public String country;
        public String city;
        public String door_no;
        public String latitude;
        public String longitude;
    }
}


