package com.mewebstudio.blogapi.config

import jakarta.servlet.RequestDispatcher
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.WebRequest
import java.util.*

@Configuration
class CustomErrorAttributes : DefaultErrorAttributes() {
    override fun getErrorAttributes(
        webRequest: WebRequest,
        options: ErrorAttributeOptions?
    ): Map<String, Any?> {
        val errorAttributes: Map<String, Any> = super.getErrorAttributes(webRequest, options)
        val errorMessage = webRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE, RequestAttributes.SCOPE_REQUEST)

        val map: MutableMap<String, Any?> = HashMap()
        map["status"] = errorAttributes["status"]
        if (errorMessage != null) {
            val message =
                (if (errorAttributes["message"] != null) errorAttributes["message"] else "Server error") as String?
            map["message"] = message
        }
        if (errorAttributes["items"] != null) {
            map["items"] = errorAttributes["items"]
        }

        return map
    }
}
