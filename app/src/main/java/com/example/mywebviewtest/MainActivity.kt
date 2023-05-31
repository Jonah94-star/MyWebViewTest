package com.example.mywebviewtest

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.mywebviewtest.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    val TAG: String = this.javaClass.simpleName

    var activity: Activity? = null
    var context: Context? = null

    // 전역 변수로 바인딩 객체 선언
    private var bind: ActivityMainBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = bind!!

    private val urlStr: String? = "https://www.nts.go.kr/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        this.activity = this;
        this.context = getApplicationContext();

        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initWebView()
    }

    // 액티비티가 파괴될 때..
    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        bind = null
        super.onDestroy()
    }

    @NonNull
    fun initWebView() {
        // https://stackoverflow.com/questions/31295237/android-webview-takes-screenshot-only-from-top-of-the-page
        // https://www.python2.net/questions-100386.htm


        // https://128june.tistory.com/48
        bind?.webviewReport?.apply {
            with(settings) {
                defaultTextEncodingName = "UTF-8" //인코딩 설정
                // Enable JavaScript
                setJavaScriptEnabled(true)
                // Enable Zoom
                setBuiltInZoomControls(true)
                setSupportZoom(true)
                setInitialScale(0)
                // Adjust web display
                setLoadWithOverviewMode(true)
                setUseWideViewPort(true)
                setDomStorageEnabled(true); // 로컬저장소 허용 여부

                setCacheMode(WebSettings.LOAD_NO_CACHE)
                WebSettings.ZoomDensity.FAR
            }

            webViewClient = object : WebViewClient() {
                // 1) 로딩 시작
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                // 2) 로딩 끝
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.i(TAG, "page finished loading $url")
                }

                // 3) 외부 브라우저가 아닌 웹뷰 자체에서 url 호출
                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    view.loadUrl(url!!)
                    return false
                }
            }

            webChromeClient = (object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    Log.d(TAG, "onPermissionRequest")
                    activity?.runOnUiThread {
                        if (request.origin.toString() == urlStr) {
                            request.grant(request.resources)
                        } else {
                            request.deny()
                        }
                    }
                }
            })
        }

        // 3. 웹페이지 호출
        if (urlStr != null) {
            bind?.webviewReport?.loadUrl(urlStr)
        }
    }
}