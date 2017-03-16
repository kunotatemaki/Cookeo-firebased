package com.rukiasoft.androidapps.cocinaconroll.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.orhanobut.logger.Logger;
import com.rukiasoft.androidapps.cocinaconroll.classes.RecipeItemOld;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ReadWriteTools {
    public ReadWriteTools(){

    }

    public List<String> loadFiles(Context mContext, FilenameFilter filter, Boolean external_storage){
        List<String> list = new ArrayList<>();
        Boolean ret;

        ret = isExternalStorageWritable();
        if(!ret){
            Logger.e("no hay external storage in loadfiles");
            return list;
        }
        // Get the directory for the app's private recipes directory.
        String path;
        if(external_storage) {
            path = getEditedStorageDir();
        }else {
            path = getOriginalStorageDir(mContext);
        }
        File file = new File(path);
        if (file.exists()) {
            String[] files = file.list(filter);
            if(files != null) {
                Collections.addAll(list, files);
            }
        }
        return list;
    }

    private List<String> loadRecipesFromOldDirectory(FilenameFilter filter){
        List<String> list = new ArrayList<>();
        Boolean ret;

        ret = isExternalStorageWritable();
        if(!ret){
            Logger.e("no hay external storage in loadrecipesfromolddirectory");
            return list;
        }
        // Get the directory for the old app's private recipes directory.
        String path = getOldEditedStorageDir();
        File file = new File(path);
        if (file.exists()) {
            String[] files = file.list(filter);
            Collections.addAll(list, files);
        }
        return list;
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    /**
     * Checks if external storage is available to at least read
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Get
     */
    public String getOriginalStorageDir(Context mContext){
        String path = mContext.getExternalFilesDir(null) + String.valueOf(File.separatorChar)
                + RecetasCookeoConstants.RECIPES_DIR + String.valueOf(File.separatorChar);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public String getEditedStorageDir(){
        File rootPath = Environment.getExternalStoragePublicDirectory("");
        String path = rootPath.getAbsolutePath() + String.valueOf(File.separatorChar) +
                RecetasCookeoConstants.BASE_DIR + String.valueOf(File.separatorChar) +
                RecetasCookeoConstants.RECIPES_DIR + String.valueOf(File.separatorChar);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    private String getOldEditedStorageDir(){
        File rootPath = Environment.getExternalStoragePublicDirectory("");
        String path = rootPath.getAbsolutePath() + String.valueOf(File.separatorChar) +
                RecetasCookeoConstants.OLD_BASE_DIR + String.valueOf(File.separatorChar) +
                RecetasCookeoConstants.RECIPES_DIR + String.valueOf(File.separatorChar);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * Read recipe from xml
     */
    public RecipeItemOld readRecipe(Context mContext, String name, Integer type) {
        RecipeItemOld recipeItemOld;
        String path = "";
        File source;
        if(type.equals(RecetasCookeoConstants.PATH_TYPE_ASSETS)) {
            InputStream inputStream;
            try {
                inputStream = mContext.getAssets().open(name);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            source = createFileFromInputStream(mContext, inputStream);
            if(source == null)
                return null;
            recipeItemOld = parseFileIntoRecipe(source);
            if(recipeItemOld == null){
                return null;
            }
            recipeItemOld.setState(RecetasCookeoConstants.FLAG_ASSETS);
            recipeItemOld.setPathRecipe(RecetasCookeoConstants.ASSETS_PATH + name);
            source.delete();
        }else {
            if (type.equals(RecetasCookeoConstants.PATH_TYPE_ORIGINAL)) {
                path = getOriginalStorageDir(mContext) + name;
            }else if (type.equals(RecetasCookeoConstants.PATH_TYPE_EDITED)) {
                path = getEditedStorageDir() + name;
            }else if (type.equals(RecetasCookeoConstants.PATH_TYPE_OLD_EDITED)) {
                path = getOldEditedStorageDir() + name;
            }
            source = new File(path);
            recipeItemOld = parseFileIntoRecipe(source);
            if(recipeItemOld == null)
                return null;
            recipeItemOld.setPathRecipe(path);
            if (type.equals(RecetasCookeoConstants.PATH_TYPE_ORIGINAL)) {
                recipeItemOld.setState(RecetasCookeoConstants.FLAG_ORIGINAL);
                if(recipeItemOld.getDate() == -1L){
                    recipeItemOld.setDate(System.currentTimeMillis());
                }
            }
        }

        if((recipeItemOld.getState() & RecetasCookeoConstants.FLAG_EDITED_PICTURE) != 0)
            recipeItemOld.setPathPicture(RecetasCookeoConstants.FILE_PATH + getEditedStorageDir() + recipeItemOld.getPicture());
        else if((recipeItemOld.getState() & RecetasCookeoConstants.FLAG_ORIGINAL) != 0)
            recipeItemOld.setPathPicture(RecetasCookeoConstants.FILE_PATH + getOriginalStorageDir(mContext) + recipeItemOld.getPicture());
        else if((recipeItemOld.getState() & RecetasCookeoConstants.FLAG_ASSETS) != 0)
            recipeItemOld.setPathPicture(RecetasCookeoConstants.ASSETS_PATH + recipeItemOld.getPicture());

        return recipeItemOld;
    }


    private File createFileFromInputStream(Context mContext, InputStream inputStream) {

        try{
            //File f = new File(mContext.getFilesDir() + "temp.txt");
            File f = getTempFile(mContext);
            if(f== null){
                return null;
            }else {
                OutputStream outputStream = new FileOutputStream(f);

                byte buffer[] = new byte[1024];
                int length;
                while((length=inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer,0,length);
                }
                outputStream.close();
                inputStream.close();
                return f;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File getTempFile(Context mContext) {
        File file;
        try {
            file = File.createTempFile("tmp.xml", null, mContext.getCacheDir());
            return file;
        } catch (IOException e) {
            e.printStackTrace();// Error while creating file
        }
        return null;
    }

    private RecipeItemOld parseFileIntoRecipe(File source){
        RecipeItemOld recipeItemOld;
        Serializer serializer = new Persister();
        try {
            recipeItemOld = serializer.read(RecipeItemOld.class, source, false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return recipeItemOld;
    }


    private void deleteImageFromOriginalPath(Context context, String name) {
        String path = getOriginalStorageDir(context) + name;
        File file = new File(path);
        if(file.exists())
            file.delete();
    }

    public String saveBitmap(Context context, Bitmap bitmap, String name){


        FileOutputStream out = null;
        String filename = "";
        File file = new File(getOriginalStorageDir(context));
        if (!file.exists()) {
            Boolean ret = file.mkdirs();
            if(!ret)
                return "";
        }
        try {
            filename = getOriginalStorageDir(context) + name;
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    public void loadImageFromPath(Context mContext, ImageView imageView, String path, int defaultImage, long version) {
        String fullPath = getOriginalStorageDir(mContext);
        File file = new File(fullPath + path);
        Glide.with(mContext)
               .load(Uri.fromFile(file))
               .centerCrop()
               .signature(new MediaStoreSignature(RecetasCookeoConstants.MIME_TYPE_PICTURE, version, 0))
               .error(defaultImage)
               .into(imageView);
    }

    public void loadImageFromPathInCircle(Context mContext, ImageView imageView, String path, int defaultImage, int version) {
        String fullPath = getOriginalStorageDir(mContext);
        File file = new File(fullPath + path);
        Glide.with(mContext)
               .load(Uri.fromFile(file))
               .centerCrop()
               .signature(new MediaStoreSignature(RecetasCookeoConstants.MIME_TYPE_PICTURE, version, 0))
               .transform(new GlideCircleTransform(mContext))
               .error(defaultImage)
               .into(imageView);
    }

    public void loadImageFromPath(Context mContext, BitmapImageViewTarget bitmapImageViewTarget, String path, int defaultImage, long version) {
        String fullPath = getOriginalStorageDir(mContext);
        File file = new File(fullPath + path);
        Glide.with(mContext)
                .load(Uri.fromFile(file))
                .asBitmap()
                .signature(new MediaStoreSignature(RecetasCookeoConstants.MIME_TYPE_PICTURE, version, 0))
                .centerCrop()
                .error(defaultImage)
                .into(bitmapImageViewTarget);
    }




    private class MyFileFilter implements FilenameFilter {

        @Override
        public boolean accept(File directory, String fileName) {
            return fileName.endsWith(".xml");
        }
    }

    public void deleteImage(Context context, String image){
        if(image != null){
            deleteImageFromOriginalPath(context, image);
        }
    }

    public boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.delete();
    }

    public List<String> loadOldEditedAndOriginalRecipes(){
        List<String> names = new ArrayList<>();
        MyFileFilter filter = new MyFileFilter();

        if(!isExternalStorageReadable()){
            Logger.e("no hay external storage in loadOldEditedAndOriginalRecipes");
            return names;
        }

        // Get the directory for the app's public recipes directory.
        String path = getEditedStorageDir();
        File file = new File(path);
        if (file.exists()) {
            String[] files = file.list(filter);
            if(files != null) {
                Collections.addAll(names, files);
            }
        }
        return names;
    }

}
