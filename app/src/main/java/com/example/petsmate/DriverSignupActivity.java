package com.example.petsmate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class DriverSignupActivity extends AppCompatActivity {

    ImageView driverIv, carIv1, carIv2;
    String driverImagePath, carImagePath1, carImagePath2, uploadURL;
    boolean[] upLoadCheck = {false,false,false};

    EditText idEt, passwordEt, password2Et, nameEt, phoneEt;
    boolean isCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_signup);
        Intent intent = getIntent();

        idEt = (EditText) findViewById(R.id.dr_id_input);
        passwordEt = (EditText) findViewById(R.id.dr_pw_input);
        password2Et = (EditText) findViewById(R.id.dr_pw2_input);
        nameEt = (EditText) findViewById(R.id.dr_name_input);
        phoneEt = (EditText) findViewById(R.id.dr_phon_input);
        driverIv = (ImageView) findViewById(R.id.dr_image_up);
        carIv1 = (ImageView) findViewById(R.id.car_image_up);
        carIv2 = (ImageView) findViewById(R.id.car2_image_up);

        uploadURL = "http://34.66.28.111:8080/DB/driverImageUpLoad.jsp";


    }

    public void onDriverImage(View v) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if (permissionCheck <= PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "허용을 해주세요.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 0);
        } else {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(i, 1);
        }
    }

    public void onCarImage1(View v) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if (permissionCheck <= PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "허용을 해주세요.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 0);
        } else {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(i, 2);
        }

    }

    public void onCarImage2(View v) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if (permissionCheck <= PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "허용을 해주세요.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 0);
        } else {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(i, 3);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    driverIv.setImageBitmap(img);
                    Log.i("driverPath", data.getData().toString());

                    driverImagePath = getRealPathFromURI(data.getData());
                    Log.i("fileName1", " " + driverImagePath);

//                    ImageUploadTask imageUploadTask = new ImageUploadTask();
//                    imageUploadTask.fileName = driverImagePath;
//                    imageUploadTask.urlString = "http://34.66.28.111:8080/DB/driverImageUpLoad.jsp";
//                    imageUploadTask.execute().get();

                    new Thread() {
                        @Override
                        public void run() {
                            DoFileUpload(uploadURL,driverImagePath);
                        }
                    }.start();

                    upLoadCheck[0] = true;

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DriverImage", e.toString());
                }
            }
        } else if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    carIv1.setImageBitmap(img);
                    Log.i("carPath1", data.getData().toString());

                    carImagePath1 = getRealPathFromURI(data.getData());
                    Log.i("carfileName1", carImagePath1);

//                    ImageUploadTask imageUploadTask = new ImageUploadTask();
//                    imageUploadTask.fileName = driverImagePath;
//                    imageUploadTask.urlString = "http://34.66.28.111:8080/DB/driverImageUpLoad.jsp";
//                    imageUploadTask.execute().get();

                    new Thread() {
                        @Override
                        public void run() {
                            DoFileUpload(uploadURL,carImagePath1);
                        }
                    }.start();

                    upLoadCheck[1] = true;

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("carImage1", e.toString());
                }
            }
        } else if (requestCode == 3) {
            if(resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    carIv2.setImageBitmap(img);
                    Log.i("carPath2", data.getData().toString());

                    carImagePath2 = getRealPathFromURI(data.getData());
                    Log.i("carFileName2", carImagePath2);

//                    ImageUploadTask imageUploadTask = new ImageUploadTask();
//                    imageUploadTask.fileName = driverImagePath;
//                    imageUploadTask.urlString = "http://34.66.28.111:8080/DB/driverImageUpLoad.jsp";
//                    imageUploadTask.execute().get();

                    new Thread() {
                        @Override
                        public void run() {
                            DoFileUpload(uploadURL,carImagePath2);
                        }
                    }.start();

                    upLoadCheck[2] = true;

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("carImage2", e.toString());
                }
            }
        }
    }

    public void onIdCheck(View v) {
        // TODO DB select 후 항목 있는지 확인 하기.
        isCheck = true;
    }

    // TODO 이미지 서버에 저장 필요
    public void onSignup(View v) { // 확인 버튼
        String id, password, password2, name, phone;
        id = idEt.getText().toString();
        password = passwordEt.getText().toString();
        password2 = password2Et.getText().toString();
        name = nameEt.getText().toString();
        phone = phoneEt.getText().toString(); // TODO 010-1111-2222 을 01011112222로 변경 절차 필요

        if (!password.equals(password2)) {
            Toast.makeText(getApplicationContext(), "비밀번호와 재확인 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (isCheck) {
            MyTask myTask = new MyTask();

            try {
                String result = myTask.execute(id, password, name, phone).get(); // JSP에 get 방식으로 요청
                result = result.trim(); // 앞, 뒤 공백 제거
                Log.i("signup insert result : ", result);

                try {
                    int resultInt = Integer.parseInt(result);

                    if (resultInt == 1 || result.equals("1")) {
                        Toast.makeText(getApplicationContext(), "회원가입 완료.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "회원가입 실패.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.i("signupResultParse", e.toString());
                    Toast.makeText(getApplicationContext(), "회원가입 실패.", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.i("DB SIGNUP ERROR", e.toString());
            }

        } else {
            Toast.makeText(getApplicationContext(), "아이디 중복 검사를 해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO 기사용으로 수정 필요
    class MyTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
                URL url = new URL("http://34.66.28.111:8080/DB/signUp.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                // 전송할 데이터. GET 방식으로 작성
//                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
                sendMsg = String.format("id=%s&password=%s&name=%s&phone=%s", strings[0], strings[1], strings[2], strings[3]);

                osw.write(sendMsg);
                osw.flush();

                //jsp와 통신 성공 시 수행
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();

                    // jsp에서 보낸 값을 받는 부분
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    // 통신 실패
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //jsp로부터 받은 리턴 값
            return receiveMsg;
        }


    }

    public String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        }
        return null;
    }


    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("getRealPath", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void DoFileUpload(String apiUrl, String absolutePath) {

        HttpFileUpload(apiUrl, "", absolutePath);

    }



    public void HttpFileUpload(String urlString, String params, String fileName) {



        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";

        try {

            File sourceFile = new File(fileName);

            DataOutputStream dos;

            if (!sourceFile.isFile()) {

                Log.e("uploadFile", "Source File not exist :" + fileName);

            } else {

                FileInputStream mFileInputStream = new FileInputStream(sourceFile);

                URL connectUrl = new URL(urlString);

                // open connection

                HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();

                conn.setDoInput(true);

                conn.setDoOutput(true);

                conn.setUseCaches(false);

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", fileName);

                // write data

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);



                int bytesAvailable = mFileInputStream.available();

                int maxBufferSize = 1024 * 1024;

                int bufferSize = Math.min(bytesAvailable, maxBufferSize);



                byte[] buffer = new byte[bufferSize];

                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);



                // read image

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);

                    bytesAvailable = mFileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                }



                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                mFileInputStream.close();

                dos.flush(); // finish upload...

                if (conn.getResponseCode() == 200) {

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer stringBuffer = new StringBuffer();

                    String line;

                    while ((line = reader.readLine()) != null) {

                        stringBuffer.append(line);

                    }

                }

                mFileInputStream.close();

                dos.close();

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }



    class ImageUploadTask extends AsyncTask<Void, Void, Void> {
        String fileName, urlString;

        @Override
        protected Void doInBackground(Void... voids) {
            String lineEnd = "\r\n";

            String twoHyphens = "--";

            String boundary = "*****";

            try {

                File sourceFile = new File(fileName);

                DataOutputStream dos;

                if (!sourceFile.isFile()) {

                    Log.e("uploadFile", "Source File not exist :" + fileName);

                } else {

                    FileInputStream mFileInputStream = new FileInputStream(sourceFile);

                    URL connectUrl = new URL(urlString);

                    // open connection

                    HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();

                    conn.setDoInput(true);

                    conn.setDoOutput(true);

                    conn.setUseCaches(false);

                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Connection", "Keep-Alive");

                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    conn.setRequestProperty("uploaded_file", fileName);

                    // write data

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);


                    int bytesAvailable = mFileInputStream.available();

                    int maxBufferSize = 1024 * 1024;

                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);


                    byte[] buffer = new byte[bufferSize];

                    int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);


                    // read image

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);

                        bytesAvailable = mFileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);

                        bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                    }


                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    mFileInputStream.close();

                    dos.flush(); // finish upload...

                    if (conn.getResponseCode() == 200) {

                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                        BufferedReader reader = new BufferedReader(tmp);

                        StringBuffer stringBuffer = new StringBuffer();

                        String line;

                        while ((line = reader.readLine()) != null) {

                            stringBuffer.append(line);

                        }

                    }

                    mFileInputStream.close();

                    dos.close();

                }

            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;

//            String lineEnd = "\r\n";
//            String twoHyphens = "--";
//            String boundary = "*****";
//
//            try {
//                Log.d("fileName", fileName);
//                FileInputStream mFileInputStream = new FileInputStream(fileName);
//                URL connectUrl = new URL(urlString);
//                Log.d("Test", "mFileInputStream  is " + mFileInputStream);
//
//                // HttpURLConnection 통신
//                HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setUseCaches(false);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Connection", "Keep-Alive");
//                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//
//                // write data
//                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
//                dos.writeBytes(twoHyphens + boundary + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
//                dos.writeBytes(lineEnd);
//
//                int bytesAvailable = mFileInputStream.available();
//                int maxBufferSize = 1024;
//                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//
//                byte[] buffer = new byte[bufferSize];
//                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//
//                Log.d("Test", "image byte is " + bytesRead);
//
//                // read image
//                while (bytesRead > 0) {
//                    dos.write(buffer, 0, bufferSize);
//                    bytesAvailable = mFileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//                }
//
//                dos.writeBytes(lineEnd);
//                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//                // close streams
//                Log.e("Test", "File is written");
//                mFileInputStream.close();
//                dos.flush();
//                // finish upload...
//
//                // get response
//                InputStream is = conn.getInputStream();
//
//                StringBuffer b = new StringBuffer();
//                for (int ch = 0; (ch = is.read()) != -1; ) {
//                    b.append((char) ch);
//                }
//                is.close();
//                Log.e("Test", b.toString());
//
//            } catch (Exception e) {
//                Log.d("Test", "exception " + e.toString());
//
//            }
//            return null;
        }

    }


}