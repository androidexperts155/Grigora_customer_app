package com.rvtechnologies.grigora.view.ui.login_signup

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

interface GoogleSignin {
    fun signInResult(task: Task<GoogleSignInAccount>)
}