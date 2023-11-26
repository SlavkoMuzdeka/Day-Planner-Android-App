package org.unibl.etf.dayplanner.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.unibl.etf.dayplanner.BuildConfig;
import org.unibl.etf.dayplanner.R;
import org.unibl.etf.dayplanner.utils.ImageSelectionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ModalBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = "ModalBottomSheet";

    private ImageSelectionListener listener;
    private String currentPhotoPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_bottom_sheet, container, false);
        view.findViewById(R.id.tv_camera).setOnClickListener((v) -> addWithCamera());
        view.findViewById(R.id.tv_gallery).setOnClickListener((v) -> addFromGallery());
        view.findViewById(R.id.tv_url).setOnClickListener((v) -> addFromURL());
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ImageSelectionListener)
            listener = (ImageSelectionListener) context;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void addWithCamera() {
        // Create an intent to open the device's camera app
        Intent takeImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there is a camera activity to handle the intent
        if (takeImageIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create a file to save the captured image
            File photoFile = createImageFile();

            // Check if the file was created successfully
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(
                        requireContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile
                );
                takeImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startCamera.launch(takeImageIntent);
            }
        }
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm ss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            this.currentPhotoPath = image.getAbsolutePath(); // Save the path for later use
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private final ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    listener.onImageSelected("file://" + this.currentPhotoPath);
                    this.currentPhotoPath = null;
                    Toast.makeText(requireContext(), R.string.image_uploaded, Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
    );

    private void addFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImages.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImages = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        int count = clipData.getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri uri = clipData.getItemAt(i).getUri();
                            listener.onImageSelected("file://" + saveImage(uri));
                        }
                        Toast.makeText(requireContext(), R.string.image_uploaded, Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else if(data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        listener.onImageSelected("file://" + saveImage(uri));
                        Toast.makeText(requireContext(), R.string.image_uploaded, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }
    );

    private String saveImage(Uri uri) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSS", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try (OutputStream outputStream = new FileOutputStream(new File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                imageFileName))) {
            try (InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                if (inputStream != null)
                    while ((bytesRead = inputStream.read(buffer)) != -1)
                        outputStream.write(buffer, 0, bytesRead);

                return new File(
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        imageFileName
                ).getAbsolutePath();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void addFromURL() {
        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());
        View promptView = layoutInflater.inflate(R.layout.url_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setView(promptView);

        final EditText editText = promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    String imageURL = editText.getText().toString();
                    if (!imageURL.isEmpty() && Patterns.WEB_URL.matcher(imageURL).matches()) {
                        listener.onImageSelected(imageURL);
                        Toast.makeText(requireContext(), R.string.image_uploaded, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(requireContext(), R.string.invalid_image_url, Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.cancel();
                    dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
