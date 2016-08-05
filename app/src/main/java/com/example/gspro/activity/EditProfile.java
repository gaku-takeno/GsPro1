package com.example.gspro.activity;import android.app.Activity;import android.app.AlertDialog;import android.content.ContentValues;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.res.Resources;import android.database.Cursor;import android.database.sqlite.SQLiteDatabase;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.net.Uri;import android.os.Bundle;import android.provider.MediaStore;import android.support.v7.app.AppCompatActivity;import android.view.Gravity;import android.view.View;import android.widget.Button;import android.widget.EditText;import android.widget.ImageView;import android.widget.RadioButton;import android.widget.RadioGroup;import android.widget.Toast;import com.example.gspro.helloworld.R;import com.example.gspro.lib.CommonConstants;import com.example.gspro.lib.ImageUtil;import com.example.gspro.lib.MyUtil;import com.example.gspro.lib.SqliteOpenHelper;import static android.util.Log.v;public class EditProfile extends AppCompatActivity {    private Button submitView;    private EditText nameView;    private EditText emailView;    private EditText addressView;    private EditText nationView;    private RadioGroup radioGroup;    private Bitmap bm_icon;    private SQLiteDatabase dbObj;    private MyUtil myutil;    private ImageUtil imageUtil;    private View.OnClickListener submit_ClickListener = new View.OnClickListener(){        public void onClick(View v) {submit_Click(v);}};    private View.OnClickListener iconChg_ClickListener = new View.OnClickListener(){        public void onClick(View v) {            commonImageTrans(EditProfile.this);        }    };    private SqliteOpenHelper dbHelper;    private String name = "";    private int user_id = 0;    private byte[] user_icon;    private int sex;    private String email = "";    private String nation = "";    private String address ="";    private ImageView my_iconView;    private Resources resource;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_edit_profile);        //Dのヘルパークラスをオブジェクト化する        dbHelper = new SqliteOpenHelper(this);        resource = getResources();        myutil = new MyUtil();        imageUtil = new ImageUtil(this);        Intent i = getIntent();        user_id = i.getIntExtra("user_id",0);        //ビューをオブジェクト変数にセットする。        setViewObject();        //リスナー登録する。        setListner();        //DBからプロフィールデータを取得し、各項目データをグローバル変数に代入しておく。        setYourProfile();        //Dから取得したデータをビューに入れる。        deployProfileData();    }    /**     */    private void setViewObject () {        submitView = (Button)findViewById(R.id.submit_btn);        my_iconView  =  (ImageView)findViewById(R.id.edit_icon);        nameView = (EditText) findViewById(R.id.edit_name);        emailView = (EditText) findViewById(R.id.edit_email);        addressView = (EditText) findViewById(R.id.edit_address);        nationView = (EditText) findViewById(R.id.edit_nation);        radioGroup = (RadioGroup) findViewById(R.id.radioSex);    }    private  void setListner () {        submitView.setOnClickListener(submit_ClickListener);        my_iconView.setOnClickListener(iconChg_ClickListener);    }    private void deployProfileData () {        nameView.setText(name);        if (user_icon != null) {            Bitmap user_icon_bm= BitmapFactory.decodeByteArray(user_icon, 0, user_icon.length);            my_iconView.setImageBitmap(user_icon_bm);        }        if (sex == 1) {            radioGroup.check(R.id.RadioMan);        } else if (sex == 2) {            radioGroup.check(R.id.RadioWoman);        }        nationView.setText(nation);        emailView.setText(email);        addressView.setText(address);    }    /**     * alreadyRegistered     */    public void setYourProfile () {        dbObj = dbHelper.getReadableDatabase();        String sql = "SELECT id,name,icon_blob,sex,email,nation,address FROM profile_table where id = "+user_id;        Cursor c = dbObj.rawQuery(sql, null);        c.moveToFirst();        if (c.getCount() > 0) {//            id = c.getInt(0);            name = c.getString(1);            user_id = c.getInt(0);            user_icon = c.getBlob(2);            sex = c.getInt(3);            email = c.getString(4);            nation = c.getString(5);            address = c.getString(6);        }        c.close();        dbObj.close();    }    protected void commonImageTrans(Context con) {        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(                con);        CharSequence[] Addrdata;        Addrdata = new CharSequence[2];        Addrdata[0] = resource.getString(R.string.library);        Addrdata[1] = resource.getString(R.string.camera);        // 表示項目とリスナの設定        alertDialogBuilder.setItems(Addrdata,                new android.content.DialogInterface.OnClickListener() {                    @Override                    public void onClick(DialogInterface arg0, int position) {                        // TODO 自動生成されたメソッド・スタブ                        // * 標準カメラアプリを起動する                        if (position == 0) {                            libraryApp_Click();                        } else if (position == 1) {                            cameraApp_Click();                        }                    }                });        // ダイアログを表示        alertDialogBuilder.create().show();    }    /**     * submit     */    private void  submit_Click (View v) {        name = nameView.getText().toString();        email = emailView.getText().toString();        address = addressView.getText().toString();        nation = nationView.getText().toString();        if (validationCheck()) {            // チェックされているラジオボタンの ID を取得します            RadioButton checkedRadioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());            boolean checked = ((RadioButton) checkedRadioButton).isChecked();            int sex = 0;            if (checked) {                String sexId = (String) checkedRadioButton.getText();                if (sexId.equals("man")) {                    sex = 1;//man                } else if (sexId.equals("woman")) {                    sex = 2;//woman                }            }            ContentValues values = new ContentValues();            values.put("name", name+"");            values.put("email", email+"");            values.put("address", address+"");            values.put("nation", nation+"");            values.put("sex", sex + "");            if (bm_icon != null) {                byte[] bt = myutil.changeBitmapToByte(bm_icon);                values.put("icon_blob", bt);            }            long y;            if (user_id > 0) {                y = updateDBCtr(values, CommonConstants.PROFILE_TABLE, "id", user_id);            } else {                y = insertDBCtr(values, CommonConstants.PROFILE_TABLE);            }            if (y > -1) {                registered();            }        }    }    /**     * プロフィール登録を実行する。     */    private void registered () {        Toast toast = Toast.makeText(getApplicationContext(), "プロフィールを登録しました。", Toast.LENGTH_LONG);        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);        toast.show();        Intent intent = new Intent(this, MainActivity.class);        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);        startActivity(intent);        finish();    }    /**     *     * @return res     */    private boolean validationCheck () {        boolean mailChecked = true;        boolean res = true;        if (!email.equals("")) {            mailChecked = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();        }        if (name.equals("")) {            Toast toast = Toast.makeText(getApplicationContext(), "名前がありません。", Toast.LENGTH_LONG);            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);            toast.show();            res = false;        } else if (address.equals("")){            Toast toast = Toast.makeText(getApplicationContext(), "住所がありません。", Toast.LENGTH_LONG);            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);            toast.show();            res = false;        } else if (!mailChecked) {            Toast toast = Toast.makeText(getApplicationContext(), "emailが正しく入力してください。", Toast.LENGTH_LONG);            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);            toast.show();            res = false;        }            return res;    }    /*    * 画像ライブラリボタンのClick押下時    */    protected void libraryApp_Click(){        // インテント設定        Intent intent = new Intent(Intent.ACTION_PICK);        // とりあえずストレージ内の全イメージ画像を対象        intent.setType("image/*");        intent.setAction(Intent.ACTION_GET_CONTENT);        // ギャラリー表示        startActivityForResult(intent, CommonConstants.REQUEST_PICK_LIB);    }    /*     * カメラボタンのClick押下時     */    protected void cameraApp_Click(){        Uri photoUri = imageUtil.getPhotoUri();        imageUtil.setPhotoUri(photoUri);        Intent intent = new Intent();        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);        intent.addCategory(Intent.CATEGORY_DEFAULT);        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);        startActivityForResult(intent, CommonConstants.REQUEST_PICK_CAM);    }    @Override    public void onActivityResult(int requestCode, int resultCode, Intent data) {        if (resultCode == Activity.RESULT_OK) {            //ライブラリから取得した写真を反映させる            if (requestCode == CommonConstants.REQUEST_PICK_LIB) {                if(data != null && data.getData() != null){                    int image_size_normal = myutil.changePxtoPd(this, CommonConstants.IMAGE_SIZE_NORMAL);                    imageUtil.setPhotoUri(data.getData());                    bm_icon = imageUtil.loadBitmap(image_size_normal,image_size_normal);                    if (imageUtil.getOrientationGallery(data.getData()) == 6 ) {                        bm_icon = myutil.rotateImg(bm_icon, 90);                    }                    my_iconView.setImageBitmap(bm_icon);                }            } else if (requestCode == CommonConstants.REQUEST_PICK_CAM) {                //カメラで撮影した画像を反映させる                int image_size_normal = myutil.changePxtoPd(this, CommonConstants.IMAGE_SIZE_NORMAL);                bm_icon= imageUtil.loadBitmap(image_size_normal,image_size_normal);                if (imageUtil.getOrientationCamera(imageUtil.getPath()) == 6 ) {                    bm_icon = myutil.rotateImg(bm_icon, 90);                }                my_iconView.setImageBitmap(bm_icon);            }        }    }    /**     * insertDBCtr     * @param values     * @param table     * @return     */    public long insertDBCtr (ContentValues values,String table) {        SQLiteDatabase db = dbHelper.getWritableDatabase();        long ret;        try {            ret = db.insert(table, null, values);        } finally {            db.close();        }        return ret;    }    /**     * db control     * @param values     * @param table     * @return     */    public long updateDBCtr (ContentValues values,String table,String key,int id) {        long ret = 0;        SQLiteDatabase db = dbHelper.getWritableDatabase();        try {            ret = db.update(table, values, key + " = " + id, null);        } finally {            db.close();        }        return ret;    }}