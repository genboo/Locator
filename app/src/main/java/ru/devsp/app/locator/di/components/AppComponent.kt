package ru.devsp.app.locator.di.components

import android.content.Context
import javax.inject.Singleton
import dagger.BindsInstance
import dagger.Component
import ru.devsp.app.locator.di.ViewModelModule
import ru.devsp.app.locator.di.modules.Prefs
import ru.devsp.app.locator.di.modules.RetrofitModule
import ru.devsp.app.locator.services.InstanceIdService
import ru.devsp.app.locator.services.MessagingService
import ru.devsp.app.locator.view.MainActivity

/**
 * Компонент di
 * Created by gen on 27.09.2017.
 */
@Component(modules = [ViewModelModule::class, RetrofitModule::class, Prefs::class])
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(activity: MainActivity)

    fun inject(service: InstanceIdService)

    fun inject(service: MessagingService)

}