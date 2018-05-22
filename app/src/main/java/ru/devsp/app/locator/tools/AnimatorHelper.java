package ru.devsp.app.locator.tools;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Помошник анимирования вьюх
 * Created by gen on 28.09.2017.
 */

public class AnimatorHelper {
    private static final long DEFAULT_DURATION = 100;

    @FunctionalInterface
    public interface AnimationListener{
        void onComplete();
    }

    private AnimatorHelper(){

    }

    /**
     * Анимация
     * @param context Контекст
     * @param view Вью, для которой хотим применить анимацию
     * @param anim Анимация
     * @param visibility Конечное состояние
     * @param duration Длительность
     * @param listener Callback при завершении анимации
     */
    public static void animate(Context context, View view, int anim, int visibility, long duration, AnimationListener listener){
        try {
            Animation animation = AnimationUtils.loadAnimation(context, anim);
            animation.setDuration(duration);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (visibility == View.VISIBLE) {
                        view.setVisibility(visibility);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (visibility == View.GONE || visibility == View.INVISIBLE) {
                        view.setVisibility(visibility);
                    }
                    if (listener != null) {
                        listener.onComplete();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //не используется
                }
            });
            view.startAnimation(animation);
        }catch (NullPointerException ex){
            Logger.INSTANCE.e(AnimatorHelper.class.getSimpleName(), ex);
            //Если все пошло не по плану, просто скрываем\показываем элемент
            view.setVisibility(visibility);
        }
    }

    /**
     * Анимация с обратным вызывом
     * @param context Контекст
     * @param view Вью, для которой хотим применить анимацию
     * @param anim Анимация
     * @param visibility Конечное состояние
     * @param listener Callback при завершении анимации
     */
    public static void animate(Context context, View view, int anim, int visibility, AnimationListener listener){
        animate(context, view, anim, visibility, DEFAULT_DURATION, listener);
    }

    /**
     * Анимация с определенной длительностью
     * @param context Контекст
     * @param view Вью, для которой хотим применить анимацию
     * @param anim Анимация
     * @param visibility Конечное состояние
     * @param duration Длительность
     */
    public static void animate(Context context, View view, int anim, int visibility, long duration){
        animate(context, view, anim, visibility, duration, null);
    }

    /**
     * Анимация
     * @param context Контекст
     * @param view Вью, для которой хотим применить анимацию
     * @param anim Анимация
     * @param visibility Конечное состояние
     */
    public static void animate(Context context, View view, int anim, int visibility){
        animate(context, view, anim, visibility, DEFAULT_DURATION, null);
    }
}
