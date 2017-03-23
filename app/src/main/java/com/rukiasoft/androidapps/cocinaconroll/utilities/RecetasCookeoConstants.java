package com.rukiasoft.androidapps.cocinaconroll.utilities;


public class RecetasCookeoConstants {
    public static final String PACKAGE_NAME = "com.rukiasoft.androidapps.cocinaconroll";

    public static final String PREFERENCE_INTERSTITIAL = PACKAGE_NAME + ".interstitial";
    public static final String RECIPES_DIR = "recipes";
    public static final String BASE_DIR = "CookingWihCookeo";
    public static final String OLD_BASE_DIR = "CocinandoconmiCookeo";

    public static final Integer FLAG_ASSETS = 1;
    public static final Integer FLAG_ORIGINAL = 2;
    public static final Integer FLAG_EDITED_PICTURE = 8;

    public static final String SEARCH_FIELD = "search_field";
    public static final String SEARCH_ICON_TYPE = "search_icon_type";
    public static final String SEARCH_NAME_TYPE = "search_name_type";
    public static final String SEARCH_ALL = "search_all";
    public static final String SEARCH_STARTERS = "search_starters";
    public static final String SEARCH_MAIN = "search_main";
    public static final String SEARCH_DESSERTS = "search_desserts";
    public static final String SEARCH_VEGETARIAN = "search_vegetarians";
    public static final String SEARCH_FAVOURITES = "search_favourites";
    public static final String SEARCH_OWN = "search_own";
    public static final String SEARCH_LATEST = "search_latest";
    public static final String SEARCH_RECIPE = "search_recipe";
    public static final String INSERT_RECIPE = "insert_recipe";

    public static final String TYPE_STARTERS = "starter";
    public static final String TYPE_MAIN = "main";
    public static final String TYPE_DESSERTS = "dessert";

    public static final String KEY_RECIPE = RecetasCookeoConstants.PACKAGE_NAME + ".recipe";
    public static final String KEY_TYPE = RecetasCookeoConstants.PACKAGE_NAME + ".type";
    public static final int REQUEST_DETAILS = 200;
    public static final int REQUEST_CREATE_RECIPE = 201;
    public static final int RESULT_UPDATE_RECIPE = 300;

    public static final String PROPERTY_DATABASE_CREATED = "database_created";
    public static final String PROPERTY_HIDE_SWIPE_DIALOG = "hide_swipe_dialog";
    public static final String PROPERTY_DEVICE_OWNER_EMAIL = "device_owner_email";
    public static final String PROPERTY_DEVICE_OWNER_NAME = "device_owner_name";
    public static final String PROPERTY_FIREBASE_ID = "firebase_id";
    public static final String PROPERTY_CAN_UPLOAD_OWN_RECIPES = "own_old_recipes";
    public static final String PROPERTY_SIGNED_IN = "signed_in";

    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd_HH-mm-ss";
    public static final String DEFAULT_PICTURE_NAME = "default_picture";
    public static final String ASSETS_PATH = "file:///android_asset/";
    public static final String FILE_PATH = "file://";


    public static final String TEMP_CAMERA_NAME = "tmp_avatar_";
    public static final String TEMP_CROP_NAME = "tmp_crop";
    public static final String ACTION_BROADCAST_UPLOADED_RECIPE = PACKAGE_NAME + ".action.ACTION_BROADCAST_UPLOADED_RECIPE";
    public static final String ACTION_BROADCAST_DELETED_RECIPE = PACKAGE_NAME + ".action.ACTION_BROADCAST_DELETED_RECIPE";

    public static final String MIME_TYPE_PICTURE = "image/jpeg";

    public static final String ALLOWED_RECIPES_NODE = "allowed_recipes";
    public static final String FORBIDDEN_RECIPES_NODE = "forbidden_recipes";
    public static final String PENDING_RECIPES_NODE = "pending_recipes";
    public static final String PERSONAL_RECIPES_NODE = "personal_recipes";

    public static final String DETAILED_RECIPES_NODE = "detailed_recipes";
    public static final String TIMESTAMP_RECIPES_NODE = "timestamp";

    public static final Integer PATH_TYPE_ASSETS = 0;
    public static final Integer PATH_TYPE_ORIGINAL = 1;
    public static final Integer PATH_TYPE_EDITED = 2;
    public static final Integer PATH_TYPE_OLD_EDITED = 3;

    public static final Long TIMEFRAME_MILI_SECONDS_DAY = (long) (1000 * 3600 * 24);
    public static final Integer TIMEFRAME_NEW_RECIPE_DAYS = 7;

    public static final String FILTER_ALL_RECIPES = PACKAGE_NAME + ".allrecipes";
    public static final String FILTER_STARTER_RECIPES = PACKAGE_NAME + ".starters";
    public static final String FILTER_MAIN_COURSES_RECIPES = PACKAGE_NAME + ".maincourses";
    public static final String FILTER_DESSERT_RECIPES = PACKAGE_NAME + ".desserts";
    public static final String FILTER_VEGETARIAN_RECIPES = PACKAGE_NAME + ".vegetarians";
    public static final String FILTER_FAVOURITE_RECIPES = PACKAGE_NAME + ".favourites";
    public static final String FILTER_OWN_RECIPES = PACKAGE_NAME + ".ownrecipes";
    public static final String FILTER_LATEST_RECIPES = PACKAGE_NAME + ".latest";

    public static final int LOADER_ID = 1;
    public static final int N_RECIPES_TO_INTERSTICIAL = 5;


    public static final String EMAIL = "rukiasoft@gmail.com";


    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 12;


    public static final String RECIPES_TABLE_NAME = "RECIPES";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ICON = "icon";
    public static final String FIELD_NAME_NORMALIZED = "NORMALIZED_NAME";

    public static final String STORAGE_PERSONAL_NODE = "personal";
    public static final String STORAGE_PENDING_NODE = "pending";
    public static final String RUKIA_TAG = "RUKIA";

    public static int LANG_SPANISH = 0;

    //Request codes
    public static final int REQUEST_CODE_ANIMATION = 101;
    public static final int REQUEST_CODE_SETTINGS = 102;
    public static final int REQUEST_CODE_SIGNING_FROM_SPLASH = 103;
    public static final int REQUEST_CODE_SIGNING_FROM_RECIPELIST = 104;
    public static final int REQUEST_CODE_GOOGLE_SIGN_IN = 104;

    public static final int FLAG_ALLOWED_RECIPE = 1;
    public static final int FLAG_FORBIDDEN_RECIPE = 2;
    public static final int FLAG_PERSONAL_RECIPE = 4;

    public static final int FLAG_NOT_UPDATE_RECIPE = 0;
    public static final int FLAG_DOWNLOAD_RECIPE = 1;
    public static final int FLAG_UPLOAD_RECIPE = 2;
    public static final int FLAG_DELETE_RECIPE = 3;
    public static final int FLAG_NOT_UPDATE_PICTURE = 0;
    public static final int FLAG_DOWNLOAD_PICTURE = 1;
    public static final int FLAG_UPLOAD_PICTURE = 2;
    public static final int FLAG_DELETE_PICTURE = 3;


}
