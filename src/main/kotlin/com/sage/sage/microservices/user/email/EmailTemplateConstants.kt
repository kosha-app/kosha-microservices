package com.sage.sage.microservices.user.email

object EmailTemplateConstants {
    const val VERIFICATION_EMAIL_SUBJECT = "🎶 Welcome to Kosha – Your OTP is Inside!"

    const val VERIFICATION_EMAIL_BODY = """
        <html>
        <body style="font-family: 'Segoe UI', sans-serif; background-color: #fafafa; padding: 30px;">
            <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 35px; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.05);">
                
                <div style="text-align: center;">
                    <img src="https://cdn-icons-png.flaticon.com/512/727/727245.png" width="64" height="64" alt="Music Icon" />
                    <h1 style="color: #e91e63; margin-bottom: 10px;">Welcome to Kosha! 🇿🇦🎧</h1>
                    <p style="font-size: 18px; color: #555;">Mzansi’s freshest "koshas" are waiting for you 🎶</p>
                </div>

                <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;" />

                <p style="font-size: 16px; color: #333;">
                    Your one-time password (OTP) to verify your Kosha account is:
                </p>

                <div style="text-align: center; margin: 25px 0;">
                    <span style="display: inline-block; font-size: 30px; letter-spacing: 4px; color: #212121; background-color: #fce4ec; padding: 15px 25px; border-radius: 10px; font-weight: bold;">
                        %s
                    </span>
                </div>

                <p style="font-size: 16px; color: #333;">
                    Use this code to complete your registration. It’s valid for <strong>{EXPIRY_DURATION} minutes</strong>.
                </p>

                <p style="font-size: 14px; color: #777; margin-top: 20px;">
                    Didn’t request this? No worries. Just ignore this message. ✌️
                </p>

                <div style="margin-top: 40px; text-align: center;">
                    <p style="font-size: 16px; color: #333;">Thanks for joining the rhythm. Let’s vibe together! 💃🕺</p>
                    <p style="font-size: 18px; color: #e91e63; font-weight: bold;">— The Kosha Team</p>
                </div>
            </div>
        </body>
        </html>
    """
}


