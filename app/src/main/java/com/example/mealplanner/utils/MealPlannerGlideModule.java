package com.example.mealplanner.utils;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.example.mealplanner.R;

@GlideModule
public class MealPlannerGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Set memory cache size (10% of available memory)
        int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));

        // Set disk cache size
        int diskCacheSizeBytes = 1024 * 1024 * 100; // 100mb
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Can register custom components here if needed
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
