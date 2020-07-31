package com.babylon.orbit2.sample.posts.app.di

import androidx.room.Room
import com.babylon.orbit2.sample.posts.app.features.postdetails.viewmodel.PostDetailsViewModel
import com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel.PostListViewModel
import com.babylon.orbit2.sample.posts.data.posts.PostDataRepository
import com.babylon.orbit2.sample.posts.data.posts.database.AvatarUrlGenerator
import com.babylon.orbit2.sample.posts.data.posts.database.Database
import com.babylon.orbit2.sample.posts.data.posts.database.PostDataDetailMapper
import com.babylon.orbit2.sample.posts.data.posts.database.PostDataOverviewMapper
import com.babylon.orbit2.sample.posts.data.posts.network.PostNetworkDataSource
import com.babylon.orbit2.sample.posts.data.posts.network.TypicodeService
import com.babylon.orbit2.sample.posts.domain.repositories.PostRepository
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

fun module() = module {
    viewModel { PostListViewModel(get()) }

    viewModel { (postId: Int) -> PostDetailsViewModel(get(), postId) }

    single {
        ObjectMapper().registerKotlinModule().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    single {
        Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create(get()))
            .baseUrl("https://jsonplaceholder.typicode.com").build()
    }

    single { get<Retrofit>().create(TypicodeService::class.java) }

    single { PostNetworkDataSource(get()) }

    single { Room.databaseBuilder(get(), Database::class.java, "main-database").build() }

    single { get<Database>().postDao() }

    single { AvatarUrlGenerator() }

    single { PostDataOverviewMapper(get()) }

    single { PostDataDetailMapper(get()) }

    single { PostDataRepository(get(), get(), get(), get()) as PostRepository }
}
