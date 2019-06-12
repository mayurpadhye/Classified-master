package mimosale.com.notification;

public class ClaimedCouponPojo {
    String id, coupon_id, user_id,shop_id, title, description,discount,start_date, end_date, no_of_claims, status1, shop_name,totalClaimedCoupons, type, coupon_image;

    public ClaimedCouponPojo(String id, String coupon_id, String user_id, String shop_id, String title, String description, String discount, String start_date, String end_date, String no_of_claims, String status1, String shop_name, String totalClaimedCoupons, String type, String coupon_image) {
        this.id = id;
        this.coupon_id = coupon_id;
        this.user_id = user_id;
        this.shop_id = shop_id;
        this.title = title;
        this.description = description;
        this.discount = discount;
        this.start_date = start_date;
        this.end_date = end_date;
        this.no_of_claims = no_of_claims;
        this.status1 = status1;
        this.shop_name = shop_name;
        this.totalClaimedCoupons = totalClaimedCoupons;
        this.type = type;
        this.coupon_image = coupon_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getNo_of_claims() {
        return no_of_claims;
    }

    public void setNo_of_claims(String no_of_claims) {
        this.no_of_claims = no_of_claims;
    }

    public String getStatus1() {
        return status1;
    }

    public void setStatus1(String status1) {
        this.status1 = status1;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getTotalClaimedCoupons() {
        return totalClaimedCoupons;
    }

    public void setTotalClaimedCoupons(String totalClaimedCoupons) {
        this.totalClaimedCoupons = totalClaimedCoupons;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCoupon_image() {
        return coupon_image;
    }

    public void setCoupon_image(String coupon_image) {
        this.coupon_image = coupon_image;
    }
}
