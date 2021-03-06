package com.zzhoujay.markdown.parser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.widget.TextView;

import com.mikepenz.octicons_typeface_library.Octicons;
import com.zzhoujay.markdown.style.CodeBlockSpan;
import com.zzhoujay.markdown.style.CodeBlockSpan2;
import com.zzhoujay.markdown.style.CodeSpan;
import com.zzhoujay.markdown.style.CustomTypeFace;
import com.zzhoujay.markdown.style.EmailSpan;
import com.zzhoujay.markdown.style.FontSpan;
import com.zzhoujay.markdown.style.LinkSpan;
import com.zzhoujay.markdown.style.MarkDownBulletSpan;
import com.zzhoujay.markdown.style.MarkDownQuoteSpan;
import com.zzhoujay.markdown.style.QuotaBulletSpan;
import com.zzhoujay.markdown.style.TodoBulletSpan;
import com.zzhoujay.markdown.style.UnderLineSpan;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-6-28.
 * StyleBuilderImpl
 */
public class StyleBuilderImpl implements StyleBuilder {

    private static final int h1_color = Color.parseColor("#333333");
    private static final int h6_color = Color.parseColor("#777777");
    private static final int quota_color = Color.parseColor("#DDDDDD");
    private static final int code_color = Color.parseColor("#F0F0F0");
    private static final int link_color = -1;
    private static final int h_under_line_color = Color.parseColor("#eeeeee");

    private static final float scale_h1 = 2.25f;
    private static final float scale_h2 = 1.75f;
    private static final float scale_h3 = 1.5f;
    private static final float scale_h4 = 1.25f;
    private static final float scale_h5 = 1, scale_h6 = 1;
    private static final float scale_normal = 1;

    private Context mContext;
    private Html.ImageGetter imageGetter;

    public StyleBuilderImpl(Context context, Html.ImageGetter imageGetter) {
        mContext = context;
        this.imageGetter = imageGetter;
    }

    @Override
    public SpannableStringBuilder em(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        FontSpan fontSpan = new FontSpan(scale_normal, Typeface.BOLD, h1_color);
        builder.setSpan(fontSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder italic(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        FontSpan fontSpan = new FontSpan(scale_normal, Typeface.ITALIC, h1_color);
        builder.setSpan(fontSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder emItalic(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        FontSpan fontSpan = new FontSpan(scale_normal, Typeface.BOLD_ITALIC, h1_color);
        builder.setSpan(fontSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder delete(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        StrikethroughSpan span = new StrikethroughSpan();
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(h1_color);
        builder.setSpan(colorSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(span, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder email(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        EmailSpan emailSpan = new EmailSpan(charSequence.toString(), link_color);
        builder.setSpan(emailSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder code(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        CodeSpan span = new CodeSpan(code_color);
        builder.setSpan(span, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder h1(CharSequence charSequence) {
        return hWithUnderLine(charSequence, scale_h1);
    }

    @Override
    public SpannableStringBuilder h2(CharSequence charSequence) {
        return hWithUnderLine(charSequence, scale_h2);
    }

    @Override
    public SpannableStringBuilder h3(CharSequence charSequence) {
        return h(charSequence, scale_h3, h1_color);
    }

    @Override
    public SpannableStringBuilder h4(CharSequence charSequence) {
        return h(charSequence, scale_h4, h1_color);
    }

    @Override
    public SpannableStringBuilder h5(CharSequence charSequence) {
        return h(charSequence, scale_h5, h1_color);
    }

    @Override
    public SpannableStringBuilder h6(CharSequence charSequence) {
        return h(charSequence, scale_h6, h6_color);
    }

    @Override
    public SpannableStringBuilder quota(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        QuoteSpan span = new MarkDownQuoteSpan(quota_color);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder ul(CharSequence charSequence, int level) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        BulletSpan bulletSpan = new MarkDownBulletSpan(level, h1_color, 0);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder ol(CharSequence charSequence, int level, int index) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        BulletSpan bulletSpan = new MarkDownBulletSpan(level, h1_color, index);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder ul2(CharSequence charSequence, int quotaLevel, int bulletLevel) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        QuotaBulletSpan bulletSpan = new QuotaBulletSpan(quotaLevel, bulletLevel, quota_color, h1_color, 0);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder ol2(CharSequence charSequence, int quotaLevel, int bulletLevel, int index) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        QuotaBulletSpan bulletSpan = new QuotaBulletSpan(quotaLevel, bulletLevel, quota_color, h1_color, index);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder todo(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        TodoBulletSpan bulletSpan = new TodoBulletSpan(h1_color, false, mContext);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder done(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        TodoBulletSpan bulletSpan = new TodoBulletSpan(h1_color, true, mContext);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder codeBlock(CharSequence... charSequence) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (CharSequence sequence: charSequence) {
            builder.append(SpannableStringBuilder.valueOf(sequence));
            builder.append("\n");
        }

        LineBackgroundSpan lineBackgroundSpan = new LineBackgroundSpan() {
            @Override
            public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
                final int paintColor = p.getColor();
                p.setColor(code_color);
                c.drawRect(new Rect(left, top, right, bottom), p);
                p.setColor(paintColor);
            }
        };
        builder.setSpan(lineBackgroundSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder codeBlock(String code) {
        return codeBlock((CharSequence[]) code.split("\n"));
    }

    @Override
    public SpannableStringBuilder link(CharSequence title, String link, String hint) {
        Pattern pattern = Pattern.compile("^com\\.lesschat\\.(.*)://(.*)");
        Matcher matcher = pattern.matcher(link);
        String drawable = "";
        if (matcher.find()) {
            String type = matcher.group(1);
            switch (type) {
                case "task":
                    drawable = String.valueOf(Octicons.Icon.oct_tasklist.getCharacter()) + " ";
                    break;
                case "file":
                    break;
                case "event":
                    break;
                case "approval":
                    break;
                default:
                    break;
            }
        }
        Octicons octicons = new Octicons();
        title = drawable + title;
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(title);
        LinkSpan linkSpan = new LinkSpan(link, link_color);
        builder.setSpan(linkSpan, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new CustomTypeFace("", octicons.getTypeface(mContext)), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder image(CharSequence title, String url, String hint) {
        if (title == null || title.length() == 0) {
            title = "$";
        }
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(title);
        Drawable drawable = null;
        if (imageGetter != null) {
            drawable = imageGetter.getDrawable(url);
        }
        if (drawable == null) {
            drawable = new ColorDrawable(Color.TRANSPARENT);
//            builder.delete(0, builder.length());
//            return builder;
        }
        ImageSpan imageSpan = new ImageSpan(drawable, url);
        builder.setSpan(imageSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @SuppressWarnings("WeakerAccess")
    protected SpannableStringBuilder h(CharSequence charSequence, float s, int color) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        FontSpan fontSpan = new FontSpan(s, Typeface.BOLD, color);
        spannableStringBuilder.setSpan(fontSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private SpannableStringBuilder hWithUnderLine(CharSequence charSequence, float s) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        int start = 0;
        FontSpan fontSpan = new FontSpan(s, Typeface.BOLD, h1_color);
        spannableStringBuilder.setSpan(fontSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Drawable underLine = new ColorDrawable(h_under_line_color);
        UnderLineSpan underLineSpan = new UnderLineSpan(underLine, 5);
        spannableStringBuilder.append('\n');
        start += charSequence.length() + 1;
        spannableStringBuilder.append("$");
        spannableStringBuilder.setSpan(underLineSpan, start, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder gap() {
        SpannableStringBuilder builder = new SpannableStringBuilder("$");
        Drawable underLine = new ColorDrawable(h_under_line_color);
        UnderLineSpan underLineSpan = new UnderLineSpan(underLine, 10);
        builder.setSpan(underLineSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }
}
