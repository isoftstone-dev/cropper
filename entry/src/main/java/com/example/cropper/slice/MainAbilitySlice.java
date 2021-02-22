package com.example.cropper.slice;

import com.crop.cropperlib.CropImage;
import com.example.cropper.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.PixelMap;

public class MainAbilitySlice extends AbilitySlice {
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00201, "MY_TAG");
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        Button cropButton = (Button) findComponentById(ResourceTable.Id_cropButton);
        cropButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Image image = (Image) findComponentById(ResourceTable.Id_image);
                CropImage cropImage = (CropImage) findComponentById(ResourceTable.Id_cropImage);

                PixelMap pixelMap = cropImage.getCroppedImage();
                image.setPixelMap(pixelMap);
            }
        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}