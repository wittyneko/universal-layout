package silicar.tutu.universal.helper.base;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

/**
 * 屏幕自适应基础值
 * Created by Brady on 2016/5/2.
 */
public class AutoBase extends AbsAutoBase {

    ///////// 单例模式 //////////

    private static class Holder{
        private static AutoBase instance = new AutoBase(null,
                DisplayBase.getInstance().displayWidth, DisplayBase.getInstance().displayHeight, true);
    }

    public static AutoBase getInstance(){
        Holder.instance.designWidth = 0f;
        Holder.instance.designHeight = 0f;
        Holder.instance.isWidth = true;
        return Holder.instance;
    }

    public static AutoBase getInstance(float designWidth, float designHeight, boolean isWidth){
        Holder.instance.designWidth = designWidth;
        Holder.instance.designHeight = designHeight;
        Holder.instance.isWidth = isWidth;
        return Holder.instance;
    }

    public static AutoBase getInstance(Context context, float designWidth, float designHeight, boolean isWidth){
        Holder.instance.designWidth = designWidth;
        Holder.instance.designHeight = designHeight;
        Holder.instance.isWidth = isWidth;
        Holder.instance.setContext(context);
        return Holder.instance;
    }

    ///////// 类定义 //////////

    private Context context;

    public AutoBase(Context context, float designWidth, float designHeight, boolean isWidth){
        this(designWidth, designHeight, 0, 0, isWidth);
        setContext(context);
    }

    public AutoBase(float designWidth, float designHeight, float widthValue, float heightValue, boolean isWidth) {
        super(designWidth, designHeight, widthValue, heightValue, isWidth);
    }

    @Override
    public int getObject() {
        obj = objScreen;
        return obj;
    }

    public AutoBase setContext(Context context) {
        this.context = context;
        if (context != null){
            DisplayBase displayBase = DisplayBase.getInstance(context);
            widthValue = displayBase.displayWidth;
            heightValue = displayBase.displayHeight;
        }
        return this;
    }

//    public static class OldBase{
//        /**
//         * 自身自适应基础值
//         * Created by Brady on 2016/5/2.
//         */
//        public static class AutoOwnBase extends AbsAutoBase {
//
//            ///////// 单例模式 //////////
//
//            private static class Holder{
//                private static AutoOwnBase instance = new AutoOwnBase(0, 0, true);
//            }
//
//            public static AutoOwnBase getInstance(){
//                return Holder.instance;
//            }
//
//            public static AutoOwnBase getInstance(float designWidth, float designHeight, boolean isWidth){
//                Holder.instance.designWidth = designWidth;
//                Holder.instance.designHeight = designHeight;
//                Holder.instance.isWidth = isWidth;
//                return Holder.instance;
//            }
//
//            ///////// 类定义 //////////
//
//            public AutoOwnBase(float designWidth, float designHeight, boolean isWidth){
//                this(designWidth, designHeight, 0f, 0f, isWidth);
//            }
//
//            public AutoOwnBase(float designWidth, float designHeight, float widthValue, float heightValue, boolean isWidth) {
//                super(designWidth, designHeight, widthValue, heightValue, isWidth);
//            }
//
//            @Override
//            public int getObject() {
//                obj = objOwn;
//                return obj;
//            }
//        }
//
//        /**
//         * 父控件自适应基础值
//         * Created by Brady on 2016/5/2.
//         */
//        public static class AutoParentBase extends AbsAutoBase {
//
//            ///////// 单例模式 //////////
//
//            private static class Holder{
//                private static AutoParentBase instance = new AutoParentBase(null, 0, 0, true);
//            }
//
//            public static AutoParentBase getInstance(){
//                return Holder.instance;
//            }
//
//            public static AutoParentBase getInstance(ViewGroup parent, float designWidth, float designHeight, boolean isWidth){
//                Holder.instance.designWidth = designWidth;
//                Holder.instance.designHeight = designHeight;
//                Holder.instance.isWidth = isWidth;
//                Holder.instance.setParent(parent);
//                return Holder.instance;
//            }
//
//            ///////// 类定义 //////////
//
//            private ViewGroup parent;
//
//            public AutoParentBase(ViewGroup parent, float designWidth, float designHeight, boolean isWidth) {
//                this(designWidth, designHeight, 0, 0, isWidth);
//                setParent(parent);
//            }
//
//            public AutoParentBase(float designWidth, float designHeight, float widthValue, float heightValue, boolean isWidth) {
//                super(designWidth, designHeight, widthValue, heightValue, isWidth);
//            }
//
//            @Override
//            public int getObject() {
//                obj = objParent;
//                return obj;
//            }
//
//            public AutoParentBase setParent(ViewGroup parent) {
//                this.parent = parent;
//                if (parent != null){
//                    widthValue = parent.getMeasuredWidth();
//                    heightValue = parent.getMeasuredHeight();
//                }
//                return this;
//            }
//        }
//
//        /**
//         * 屏幕自适应基础值
//         * Created by Brady on 2016/5/2.
//         */
//        public static class AutoScreenBase extends AbsAutoBase {
//
//            ///////// 单例模式 //////////
//
//            private static class Holder{
//                private static AutoScreenBase instance = new AutoScreenBase(null, 0, 0, true);
//            }
//
//            public static AutoScreenBase getInstance(){
//                return Holder.instance;
//            }
//
//            public static AutoScreenBase getInstance(Context context, float designWidth, float designHeight, boolean isWidth){
//                Holder.instance.designWidth = designWidth;
//                Holder.instance.designHeight = designHeight;
//                Holder.instance.isWidth = isWidth;
//                Holder.instance.setContext(context);
//                return Holder.instance;
//            }
//
//            ///////// 类定义 //////////
//
//            private Context context;
//
//            public AutoScreenBase(Context context, float designWidth, float designHeight, boolean isWidth){
//                this(designWidth, designHeight, 0, 0, isWidth);
//                setContext(context);
//            }
//
//            public AutoScreenBase(float designWidth, float designHeight, float widthValue, float heightValue, boolean isWidth) {
//                super(designWidth, designHeight, widthValue, heightValue, isWidth);
//            }
//
//            @Override
//            public int getObject() {
//                obj = objScreen;
//                return obj;
//            }
//
//            public AutoScreenBase setContext(Context context) {
//                this.context = context;
//                if (context != null){
//                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//                    widthValue = displayMetrics.widthPixels;
//                    heightValue = displayMetrics.heightPixels;
//                }
//                return this;
//            }
//        }
//    }
}
