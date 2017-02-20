package com.rukiasoft.androidapps.cocinaconroll.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.rukiasoft.androidapps.cocinaconroll.R;
import com.rukiasoft.androidapps.cocinaconroll.classes.PreinstalledRecipeNamesList;
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
    //private final Context mContext;
    private final String TAG = LogHelper.makeLogTag(ReadWriteTools.class);
    public ReadWriteTools(){

    }

    public List<String> loadFiles(Context mContext, FilenameFilter filter, Boolean external_storage){
        List<String> list = new ArrayList<>();
        Boolean ret;

        ret = isExternalStorageWritable();
        if(!ret){
            LogHelper.e(TAG, "no hay external storage in loadfiles");
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
            LogHelper.e(TAG, "no hay external storage in loadrecipesfromolddirectory");
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

    public String getOldEditedStorageDir(){
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

    public File getOldBaseEditedStorageDirToBeDeleted(){
        File rootPath = Environment.getExternalStoragePublicDirectory("");
        String path = rootPath.getAbsolutePath() + String.valueOf(File.separatorChar) +
                RecetasCookeoConstants.OLD_BASE_DIR;
        return new File(path);
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

    /*public void saveRecipeOnOrigialPath(RecipeItemOld recipe){
        String path = getOriginalStorageDir();
        saveRecipe(recipe, path);
    }*/

    public String saveRecipeOnEditedPath(Context mContext, RecipeItemOld recipe){
        String dir = getEditedStorageDir();
        String pathFile = recipe.getPathRecipe();
        String name;
        if(pathFile == null || pathFile.isEmpty()) {
            Tools mTools = new Tools();
            name = "own_" + mTools.getCurrentDate(mContext) + ".xml";
        }else{
            Uri uri = Uri.parse(pathFile);
            name = uri.getLastPathSegment();
        }
        return saveRecipe(mContext, recipe, dir, name);
    }

    public String saveRecipe(Context mContext, RecipeItemOld recipe, String dir, String name){
        if(!isExternalStorageWritable()){
            if(mContext instanceof AppCompatActivity) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_storage_available), Toast.LENGTH_LONG)
                .show();
            }
            LogHelper.e(TAG, "no hay external storage in saverecipe");
            return "";
        }

        File file = new File(dir);
        if (!file.exists()) {
            if(!file.mkdirs())
                return "";
        }

        Serializer serializer = new Persister();
        String path = dir.concat(name);
        File result = new File(path);

        try {
            serializer.write(recipe, result);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public void deleteRecipe(RecipeItemOld recipeItemOld){

        try {
            File file = new File(recipeItemOld.getPathRecipe());
            if (file.exists())
                file.delete();
            if ((recipeItemOld.getState() & RecetasCookeoConstants.FLAG_EDITED_PICTURE) != 0) {
                file = new File(String.valueOf(Uri.parse(recipeItemOld.getPathPicture())));
                if (file.exists()) {
                    file.delete();
                } else {
                    Uri uri = Uri.fromFile(file);
                    String deletePath = getEditedStorageDir() + uri.getLastPathSegment();
                    file = new File(deletePath);
                    if (file.exists())
                        file.delete();
                }
            }
        }catch(Exception e){
            // TODO: 14/1/17 aquí mandaba excepción con ACRA
        }
    }

    public void deleteRecipe(String path){

        try {
            File file = new File(path);
            if (file.exists())
                file.delete();
        }catch(Exception e){
            // TODO: 14/1/17 aquí mandaba excepción con ACRA
        }
    }

    public List<String> loadRecipesFromAssets(Context mContext) {

        List<String> list;
        File source;
        InputStream inputStream;
        try {
            inputStream = mContext.getAssets().open("preinstalled_recipes.xml");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        source = createFileFromInputStream(mContext, inputStream);
        list = parseFileIntoRecipeList(source);
        if (source != null) {
            source.delete();
        }

        return list;
    }

    private List<String> parseFileIntoRecipeList(File source){

        Serializer serializer = new Persister();
        PreinstalledRecipeNamesList preinstalledRecipeNames;
        try {
            preinstalledRecipeNames = serializer.read(PreinstalledRecipeNamesList.class, source);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return preinstalledRecipeNames.getPreinstalledRecipeNameListAsListOfStrings();
    }

    public void deleteImageFromEditedPath(String name) {
        Uri uri = Uri.parse(name);
        name =  uri.getLastPathSegment();
        String path = getEditedStorageDir() + name;
        File file = new File(path);
        if(file.exists())
            file.delete();
    }

    public String saveBitmap(Bitmap bitmap, String name){


        FileOutputStream out = null;
        String filename = "";
        File file = new File(getEditedStorageDir());
        if (!file.exists()) {
            Boolean ret = file.mkdirs();
            if(!ret)
                return "";
        }
        try {
            filename = getEditedStorageDir() + name;
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
        return RecetasCookeoConstants.FILE_PATH.concat(filename);
    }

    public void loadImageFromPath(Context mContext, ImageView imageView, String path, int defaultImage, long version) {
        String fullPath = getOriginalStorageDir(mContext);
        StringBuilder stringBuilder = new StringBuilder(fullPath);
        stringBuilder.append(path);
        Glide.with(mContext)
               .load(Uri.parse(stringBuilder.toString()))
               .centerCrop()
               .signature(new MediaStoreSignature(RecetasCookeoConstants.MIME_TYPE_PICTURE, version, 0))
               .error(defaultImage)
               .into(imageView);
    }

    public void loadImageFromPathInCircle(Context mContext, ImageView imageView, String path, int defaultImage, int version) {
       Glide.with(mContext)
               .load(Uri.parse(path))
               .centerCrop()
               .signature(new MediaStoreSignature(RecetasCookeoConstants.MIME_TYPE_PICTURE, version, 0))
               .transform(new GlideCircleTransform(mContext))
               .error(defaultImage)
               .into(imageView);
    }

    public void loadImageFromPath(Context mContext, BitmapImageViewTarget bitmapImageViewTarget, String path, int defaultImage, int version) {
        Glide.with(mContext)
                .load(Uri.parse(path))
                .asBitmap()
                .signature(new MediaStoreSignature(RecetasCookeoConstants.MIME_TYPE_PICTURE, version, 0))
                .centerCrop()
                .error(defaultImage)
                .into(bitmapImageViewTarget);
    }

    public void share(final Activity activity, RecipeItemOld recipe)
    {
        //need to "send multiple" to get more than one attachment
        Tools tools = new Tools();
        Boolean installed = tools.isPackageInstalled("com.google.android.gm", activity);
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("message/rfc822");
        if(installed)
            emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{RecetasCookeoConstants.EMAIL});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, recipe.getName());
        String sender = String.format(activity.getResources().getString(R.string.sender), recipe.getAuthor());
        emailIntent.putExtra(Intent.EXTRA_TEXT, sender);
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<>();
        //convert from paths to Android friendly Parcelable Uri's
        File fileXml = new File(recipe.getPathRecipe());
        Uri u = Uri.fromFile(fileXml);
        uris.add(u);
        if((recipe.getState()& RecetasCookeoConstants.FLAG_EDITED_PICTURE) != 0) {
            Uri uri = Uri.parse(recipe.getPathPicture());
            String name = uri.getLastPathSegment();
            File fileJpg = new File(getEditedStorageDir() + name);
            u = Uri.fromFile(fileJpg);
            uris.add(u);
        }

        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(!installed){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            // Get the layout inflater

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle(activity.getResources().getString(R.string.email_alert_title))
                    .setMessage(activity.getResources().getString(R.string.email_alert_body))
                    .setPositiveButton(activity.getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            activity.startActivity(emailIntent);
                        }
                    });

            builder.show();
        }else
            activity.startActivity(emailIntent);
    }

    public String getZipsStorageDir(Context mContext){
        //create the dir if dont exist
        String path = mContext.getExternalFilesDir(null) + String.valueOf(File.separatorChar)
                + RecetasCookeoConstants.ZIPS_DIR + String.valueOf(File.separatorChar);
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }
        return path;
    }




    public RecipeItemOld readRecipeInfo(Context mContext, String pathRecipe) {
        RecipeItemOld recipeItemOld;
        File source;
        if(pathRecipe == null){
            Exception caughtException = new Exception("Error intentado leer una receta sin pathRecipe");
            // TODO: 14/1/17 aquí mandaba excepción con ACRA return null;
        }

        if(pathRecipe.contains(RecetasCookeoConstants.ASSETS_PATH)) {
            Uri uri = Uri.parse(pathRecipe);
            String name =  uri.getLastPathSegment();
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
            //recipeItemOld.setFileName(name);
            recipeItemOld.setPathRecipe(RecetasCookeoConstants.ASSETS_PATH + "/" + name);
            source.delete();
        }else {
            source = new File(pathRecipe);
            recipeItemOld = parseFileIntoRecipe(source);
            if(recipeItemOld == null)
                return null;
            recipeItemOld.setPathRecipe(pathRecipe);

        }

        return recipeItemOld;

    }


    private class MyFileFilter implements FilenameFilter {

        @Override
        public boolean accept(File directory, String fileName) {
            return fileName.endsWith(".xml");
        }
    }

    public void deleteImage(String image){
        if(image != null){
            deleteImageFromEditedPath(image);
        }
        /*if(file != null){
            deleteRecipe(file);
        }*/
    }

    public boolean deleteFile(Uri path){
        return deleteFile(path.getPath());
    }

    public boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.delete();
    }

    public List<String> loadOldEditedAndOriginalRecipes(Context context){
        List<String> names = new ArrayList<>();
        MyFileFilter filter = new MyFileFilter();

        if(!isExternalStorageReadable()){
            LogHelper.e(TAG, "no hay external storage in loadOldEditedAndOriginalRecipes");
            return names;
        }

        // Get the directory for the app's public recipes directory.
        String path = getEditedStorageDir();
        File file = new File(path);
        if (file.exists()) {
            String[] files = file.list(filter);
            Collections.addAll(names, files);
        }
        return names;
    }

}
