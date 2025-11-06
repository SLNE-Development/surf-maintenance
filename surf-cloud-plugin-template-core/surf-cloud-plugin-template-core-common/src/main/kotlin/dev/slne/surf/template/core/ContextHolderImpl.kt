package dev.slne.surf.template.core

import com.google.auto.service.AutoService
import dev.slne.surf.template.api.common.InternalContextHolder
import dev.slne.surf.template.api.common.util.InternalApi
import org.springframework.context.ApplicationContext

@OptIn(InternalApi::class)
@AutoService(InternalContextHolder::class)
class ContextHolderImpl : InternalContextHolder {
    override lateinit var context: ApplicationContext

    companion object {
        val instance = InternalContextHolder.instance as ContextHolderImpl
    }
}