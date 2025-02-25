package com.kls.dsgcodechallenge.di

import android.content.Context
import android.os.StatFs
import com.kls.dsgcodechallenge.network.AuthInterceptor
import com.kls.dsgcodechallenge.network.WebService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    private const val FETCH_CACHE = "fetch-cache"
    private const val MIN_CACHE_SIZE = 5L * 1024L * 1024L // 5MB
    private const val MAX_CACHE_SIZE = 50L * 1024L * 1024L // 50MB

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext ctx: Context): Cache {
        return with (File(ctx.cacheDir, FETCH_CACHE)) {
            if (!exists())
                mkdirs()
            Cache(this, calculateDiskCacheSize(this))
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache?, authInterceptor: AuthInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .cache(cache)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Singleton
    @Provides
    fun provideWebService(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
        @ApplicationContext context: Context
    ): WebService {
        return Retrofit.Builder()
            .baseUrl(WebService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
            .create(WebService::class.java)
    }

    private fun calculateDiskCacheSize(dir: File): Long {
        var size = MIN_CACHE_SIZE

        try {
            val statFs = StatFs(dir.absolutePath)
            val available = statFs.blockCountLong * statFs.blockSizeLong
            size = available / 50L
        } catch (ignored: IllegalArgumentException) {
        }

        return max(min(size, MAX_CACHE_SIZE), MIN_CACHE_SIZE)
    }
}