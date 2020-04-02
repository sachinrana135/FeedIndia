package com.alfanse.feedindia.di

import com.alfanse.feedindia.ui.donordetails.DonorDetailsActivity
import com.alfanse.feedindia.ui.mobileauth.CodeVerificationActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class,
        ApiModule::class
    ]
)
interface AppComponent {
    fun inject(donorDetailsActivity: DonorDetailsActivity)
    fun inject(codeVerificationActivity: CodeVerificationActivity)
}
