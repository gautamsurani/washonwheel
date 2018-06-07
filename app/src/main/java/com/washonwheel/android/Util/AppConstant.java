package com.washonwheel.android.Util;

/*
 * Created by welcome on 11-12-2017.
 */

public final class AppConstant {

    private static final String API_URL = "http://www.washonwheel.com/api/index.php?view=";

    public static final String API_GET_CITY = API_URL + "city";

    public static final String API_REGISTRATION = API_URL + "register&sname=";

    public static final String API_LOGIN = API_URL + "login&username=";

    public static final String API_LOGIN_WITH_FB = API_URL + "social_login&semail=";

    public static final String API_SHARE_MSG = API_URL + "share_msg&userID=";

    public static final String API_GET_CONTACT_US = API_URL + "contact_details&userID=";

    public static final String API_INSERT_HELP = API_URL + "help&name=";

    public static final String API_ABOUT = API_URL + "about";

    public static final String API_FORGET = API_URL + "forgot_password&username=";

    public static final String API_GET_SERVICE_HISTORY = API_URL + "mybooking&userID=";

    public static final String API_WOW_SERVICE = API_URL + "wow_services";

    public static final String API_WOW_SERVICE_DETAIL = API_URL + "wow_services&wow_id=";

    public static final String API_BOOK_SERVICE_DATA = API_URL + "model&userID=";

    public static final String API_SERVICE_LIST = API_URL + "services&typeID=";

    public static final String API_OFFERS = API_URL + "offer_list&pagecode=";

    public static final String API_CHANGE_PROFILE = API_URL + "change_info";

    public static final String API_CHANGE_PASSWORD = API_URL + "change_pass&userID=";

    public static final String API_USER_DATA = API_URL + "get_info&userID=";

    public static final String API_CHECKOUT_DATA = API_URL + "payment_data&userID=";

    public static final String API_COUPON = API_URL + "package_offer&userID=";

    public static final String API_COUPON_DATA = API_URL + "package_discount_coupon&userID=";

    public static final String API_APPLY_COUPON = API_URL + "discount_coupon&userID=";

    public static final String API_BOOK_SERVICE = API_URL + "book_service&userID=";

    public static final String API_MY_PACKAGE = API_URL + "my_package&userID=";

    public static final String ADI_TRANSACTION_DATA = API_URL + "wallet_transction&userID=";

    public static final String API_ADD_MONEY = API_URL + "add_money&userID=";

    public static final String API_LEAD_DETAIL = API_URL + "booking_detail&leadID=";

    public static final String API_INSERT_TOKEN = API_URL + "dashboard&userID=";

    public static final String API_VEHICLE_LIST = API_URL + "myvehicle&userID=";

    public static final String API_ADD_CAR = API_URL + "add_vehicle&userID=";

    public static final String API_DELETE_CAR = API_URL + "delete_vehicle&vehID=";
}