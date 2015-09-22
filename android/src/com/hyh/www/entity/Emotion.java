package com.hyh.www.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechEntity;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.basic.GezitechEntity.TableInfo;
import com.gezitech.entity.PageList;
import com.gezitech.util.StringUtil;
import com.hyh.www.R;

/***
 * @author xiaobai 2014-10-15
 * @todo( 表情实体 )
 */
@TableInfo(tableName = "emotion")
public class Emotion extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String title;
	@FieldInfo
	public String emotion;
	@FieldInfo
	public String filename;
	@FieldInfo
	public int drawable;
	private ArrayList<GezitechEntity_I> emojis;
	private static HashMap<String, Integer> emojiMap = new HashMap<String, Integer>();
	private static Emotion mEmotion;
	public static Emotion getInstace() {
		if (mEmotion == null) {
			mEmotion = new Emotion();
		}
		return mEmotion;
	}
	/**
	 * 默认表情列表
	 * 
	 * @return
	 */
	public static ArrayList<GezitechEntity_I> getDefaultList() {
		PageList list = new PageList();
		list.add(new Emotion(1,"可爱 ","[可爱]", "emoji_1.png",R.drawable.emoji_1 ));
		emojiMap.put("[可爱]",R.drawable.emoji_1);
		list.add(new Emotion(2,"笑脸 ","[笑脸]", "emoji_2.png",R.drawable.emoji_2 ));
		emojiMap.put("[笑脸]",R.drawable.emoji_2);
		list.add(new Emotion(3,"囧 ","[囧]", "emoji_3.png",R.drawable.emoji_3 ));
		emojiMap.put("[囧]",R.drawable.emoji_3);
		list.add(new Emotion(4,"生气 ","[生气]", "emoji_4.png",R.drawable.emoji_4 ));
		emojiMap.put("[生气]",R.drawable.emoji_4);
		list.add(new Emotion(5,"鬼脸 ","[鬼脸]", "emoji_5.png",R.drawable.emoji_5 ));
		emojiMap.put("[鬼脸]",R.drawable.emoji_5);
		list.add(new Emotion(6,"花心 ","[花心]", "emoji_6.png",R.drawable.emoji_6 ));
		emojiMap.put("[花心]",R.drawable.emoji_6);
		list.add(new Emotion(7,"害怕 ","[害怕]", "emoji_7.png",R.drawable.emoji_7 ));
		emojiMap.put("[害怕]",R.drawable.emoji_7);
		list.add(new Emotion(8,"我汗 ","[我汗]", "emoji_8.png",R.drawable.emoji_8 ));
		emojiMap.put("[我汗]",R.drawable.emoji_8);
		list.add(new Emotion(9,"尴尬 ","[尴尬]", "emoji_9.png",R.drawable.emoji_9 ));
		emojiMap.put("[尴尬]",R.drawable.emoji_9);
		list.add(new Emotion(10,"哼哼 ","[哼哼]", "emoji_10.png",R.drawable.emoji_10 ));
		emojiMap.put("[哼哼]",R.drawable.emoji_10);
		list.add(new Emotion(11,"忧郁 ","[忧郁]", "emoji_11.png",R.drawable.emoji_11 ));
		emojiMap.put("[忧郁]",R.drawable.emoji_11);
		list.add(new Emotion(12,"呲牙 ","[呲牙]", "emoji_12.png",R.drawable.emoji_12 ));
		emojiMap.put("[呲牙]",R.drawable.emoji_12);
		list.add(new Emotion(13,"媚眼 ","[媚眼]", "emoji_13.png",R.drawable.emoji_13 ));
		emojiMap.put("[媚眼]",R.drawable.emoji_13);
		list.add(new Emotion(14,"累 ","[累]", "emoji_14.png",R.drawable.emoji_14 ));
		emojiMap.put("[累]",R.drawable.emoji_14);
		list.add(new Emotion(15,"苦逼 ","[苦逼]", "emoji_15.png",R.drawable.emoji_15 ));
		emojiMap.put("[苦逼]",R.drawable.emoji_15);
		list.add(new Emotion(16,"瞌睡 ","[瞌睡]", "emoji_16.png",R.drawable.emoji_16 ));
		emojiMap.put("[瞌睡]",R.drawable.emoji_16);
		list.add(new Emotion(17,"哎呀 ","[哎呀]", "emoji_17.png",R.drawable.emoji_17 ));
		emojiMap.put("[哎呀]",R.drawable.emoji_17);
		list.add(new Emotion(18,"刺瞎 ","[刺瞎]", "emoji_18.png",R.drawable.emoji_18 ));
		emojiMap.put("[刺瞎]",R.drawable.emoji_18);
		list.add(new Emotion(19,"哭 ","[哭]", "emoji_19.png",R.drawable.emoji_19 ));
		emojiMap.put("[哭]",R.drawable.emoji_19);
		list.add(new Emotion(20,"激动 ","[激动]", "emoji_20.png",R.drawable.emoji_20 ));
		emojiMap.put("[激动]",R.drawable.emoji_20);
		list.add(new Emotion(21,"难过 ","[难过]", "emoji_21.png",R.drawable.emoji_21 ));
		emojiMap.put("[难过]",R.drawable.emoji_21);
		list.add(new Emotion(22,"害羞 ","[害羞]", "emoji_22.png",R.drawable.emoji_22 ));
		emojiMap.put("[害羞]",R.drawable.emoji_22);
		list.add(new Emotion(23,"高兴 ","[高兴]", "emoji_23.png",R.drawable.emoji_23 ));
		emojiMap.put("[高兴]",R.drawable.emoji_23);
		list.add(new Emotion(24,"愤怒 ","[愤怒]", "emoji_24.png",R.drawable.emoji_24 ));
		emojiMap.put("[愤怒]",R.drawable.emoji_24);
		list.add(new Emotion(25,"亲 ","[亲]", "emoji_25.png",R.drawable.emoji_25 ));
		emojiMap.put("[亲]",R.drawable.emoji_25);
		list.add(new Emotion(26,"飞吻 ","[飞吻]", "emoji_26.png",R.drawable.emoji_26 ));
		emojiMap.put("[飞吻]",R.drawable.emoji_26);
		list.add(new Emotion(27,"得意 ","[得意]", "emoji_27.png",R.drawable.emoji_27 ));
		emojiMap.put("[得意]",R.drawable.emoji_27);
		list.add(new Emotion(28,"惊恐 ","[惊恐]", "emoji_28.png",R.drawable.emoji_28 ));
		emojiMap.put("[惊恐]",R.drawable.emoji_28);
		list.add(new Emotion(29,"口罩 ","[口罩]", "emoji_29.png",R.drawable.emoji_29 ));
		emojiMap.put("[口罩]",R.drawable.emoji_29);
		list.add(new Emotion(30,"惊讶 ","[惊讶]", "emoji_30.png",R.drawable.emoji_30 ));
		emojiMap.put("[惊讶]",R.drawable.emoji_30);
		list.add(new Emotion(31,"委屈 ","[委屈]", "emoji_31.png",R.drawable.emoji_31 ));
		emojiMap.put("[委屈]",R.drawable.emoji_31);
		list.add(new Emotion(32,"生病 ","[生病]", "emoji_32.png",R.drawable.emoji_32 ));
		emojiMap.put("[生病]",R.drawable.emoji_32);
		list.add(new Emotion(33,"红心 ","[红心]", "emoji_33.png",R.drawable.emoji_33 ));
		emojiMap.put("[红心]",R.drawable.emoji_33);
		list.add(new Emotion(34,"心碎 ","[心碎]", "emoji_34.png",R.drawable.emoji_34 ));
		emojiMap.put("[心碎]",R.drawable.emoji_34);
		list.add(new Emotion(35,"玫瑰 ","[玫瑰]", "emoji_35.png",R.drawable.emoji_35 ));
		emojiMap.put("[玫瑰]",R.drawable.emoji_35);
		list.add(new Emotion(36,"花 ","[花]", "emoji_36.png",R.drawable.emoji_36 ));
		emojiMap.put("[花]",R.drawable.emoji_36);
		list.add(new Emotion(37,"外星人 ","[外星人]", "emoji_37.png",R.drawable.emoji_37 ));
		emojiMap.put("[外星人]",R.drawable.emoji_37);
		list.add(new Emotion(38,"金牛座 ","[金牛座]", "emoji_38.png",R.drawable.emoji_38 ));
		emojiMap.put("[金牛座]",R.drawable.emoji_38);
		list.add(new Emotion(39,"双子座 ","[双子座]", "emoji_39.png",R.drawable.emoji_39 ));
		emojiMap.put("[双子座]",R.drawable.emoji_39);
		list.add(new Emotion(40,"巨蟹座 ","[巨蟹座]", "emoji_40.png",R.drawable.emoji_40 ));
		emojiMap.put("[巨蟹座]",R.drawable.emoji_40);
		list.add(new Emotion(41,"狮子座 ","[狮子座]", "emoji_41.png",R.drawable.emoji_41 ));
		emojiMap.put("[狮子座]",R.drawable.emoji_41);
		list.add(new Emotion(42,"处女座 ","[处女座]", "emoji_42.png",R.drawable.emoji_42 ));
		emojiMap.put("[处女座]",R.drawable.emoji_42);
		list.add(new Emotion(43,"天平座 ","[天平座]", "emoji_43.png",R.drawable.emoji_43 ));
		emojiMap.put("[天平座]",R.drawable.emoji_43);
		list.add(new Emotion(44,"天蝎座 ","[天蝎座]", "emoji_44.png",R.drawable.emoji_44 ));
		emojiMap.put("[天蝎座]",R.drawable.emoji_44);
		list.add(new Emotion(45,"射手座 ","[射手座]", "emoji_45.png",R.drawable.emoji_45 ));
		emojiMap.put("[射手座]",R.drawable.emoji_45);
		list.add(new Emotion(46,"摩羯座 ","[摩羯座]", "emoji_46.png",R.drawable.emoji_46 ));
		emojiMap.put("[摩羯座]",R.drawable.emoji_46);
		list.add(new Emotion(47,"水瓶座 ","[水瓶座]", "emoji_47.png",R.drawable.emoji_47 ));
		emojiMap.put("[水瓶座]",R.drawable.emoji_47);
		list.add(new Emotion(48,"白羊座 ","[白羊座]", "emoji_48.png",R.drawable.emoji_48 ));
		emojiMap.put("[白羊座]",R.drawable.emoji_48);
		list.add(new Emotion(49,"双鱼座 ","[双鱼座]", "emoji_49.png",R.drawable.emoji_49 ));
		emojiMap.put("[双鱼座]",R.drawable.emoji_49);
		list.add(new Emotion(50,"星座 ","[星座]", "emoji_50.png",R.drawable.emoji_50 ));
		emojiMap.put("[星座]",R.drawable.emoji_50);
		list.add(new Emotion(51,"男孩 ","[男孩]", "emoji_51.png",R.drawable.emoji_51 ));
		emojiMap.put("[男孩]",R.drawable.emoji_51);
		list.add(new Emotion(52,"女孩 ","[女孩]", "emoji_52.png",R.drawable.emoji_52 ));
		emojiMap.put("[女孩]",R.drawable.emoji_52);
		list.add(new Emotion(53,"嘴唇 ","[嘴唇]", "emoji_53.png",R.drawable.emoji_53 ));
		emojiMap.put("[嘴唇]",R.drawable.emoji_53);
		list.add(new Emotion(54,"爸爸 ","[爸爸]", "emoji_54.png",R.drawable.emoji_54 ));
		emojiMap.put("[爸爸]",R.drawable.emoji_54);
		list.add(new Emotion(55,"妈妈 ","[妈妈]", "emoji_55.png",R.drawable.emoji_55 ));
		emojiMap.put("[妈妈]",R.drawable.emoji_55);
		list.add(new Emotion(56,"衣服 ","[衣服]", "emoji_56.png",R.drawable.emoji_56 ));
		emojiMap.put("[衣服]",R.drawable.emoji_56);
		list.add(new Emotion(57,"皮鞋 ","[皮鞋]", "emoji_57.png",R.drawable.emoji_57 ));
		emojiMap.put("[皮鞋]",R.drawable.emoji_57);
		list.add(new Emotion(58,"照相 ","[照相]", "emoji_58.png",R.drawable.emoji_58 ));
		emojiMap.put("[照相]",R.drawable.emoji_58);
		list.add(new Emotion(59,"电话 ","[电话]", "emoji_59.png",R.drawable.emoji_59 ));
		emojiMap.put("[电话]",R.drawable.emoji_59);
		list.add(new Emotion(60,"石头 ","[石头]", "emoji_60.png",R.drawable.emoji_60 ));
		emojiMap.put("[石头]",R.drawable.emoji_60);
		list.add(new Emotion(61,"胜利 ","[胜利]", "emoji_61.png",R.drawable.emoji_61 ));
		emojiMap.put("[胜利]",R.drawable.emoji_61);
		list.add(new Emotion(62,"禁止 ","[禁止]", "emoji_62.png",R.drawable.emoji_62 ));
		emojiMap.put("[禁止]",R.drawable.emoji_62);
		list.add(new Emotion(63,"滑雪 ","[滑雪]", "emoji_63.png",R.drawable.emoji_63 ));
		emojiMap.put("[滑雪]",R.drawable.emoji_63);
		list.add(new Emotion(64,"高尔夫 ","[高尔夫]", "emoji_64.png",R.drawable.emoji_64 ));
		emojiMap.put("[高尔夫]",R.drawable.emoji_64);
		list.add(new Emotion(65,"网球 ","[网球]", "emoji_65.png",R.drawable.emoji_65 ));
		emojiMap.put("[网球]",R.drawable.emoji_65);
		list.add(new Emotion(66,"棒球 ","[棒球]", "emoji_66.png",R.drawable.emoji_66 ));
		emojiMap.put("[棒球]",R.drawable.emoji_66);
		list.add(new Emotion(67,"冲浪 ","[冲浪]", "emoji_67.png",R.drawable.emoji_67 ));
		emojiMap.put("[冲浪]",R.drawable.emoji_67);
		list.add(new Emotion(68,"足球 ","[足球]", "emoji_68.png",R.drawable.emoji_68 ));
		emojiMap.put("[足球]",R.drawable.emoji_68);
		list.add(new Emotion(69,"小鱼 ","[小鱼]", "emoji_69.png",R.drawable.emoji_69 ));
		emojiMap.put("[小鱼]",R.drawable.emoji_69);
		list.add(new Emotion(70,"问号 ","[问号]", "emoji_70.png",R.drawable.emoji_70 ));
		emojiMap.put("[问号]",R.drawable.emoji_70);
		list.add(new Emotion(71,"叹号 ","[叹号]", "emoji_71.png",R.drawable.emoji_71 ));
		emojiMap.put("[叹号]",R.drawable.emoji_71);
		list.add(new Emotion(179,"顶 ","[顶]", "emoji_179.png",R.drawable.emoji_179 ));
		emojiMap.put("[顶]",R.drawable.emoji_179);
		list.add(new Emotion(180,"写字 ","[写字]", "emoji_180.png",R.drawable.emoji_180 ));
		emojiMap.put("[写字]",R.drawable.emoji_180);
		list.add(new Emotion(181,"衬衫 ","[衬衫]", "emoji_181.png",R.drawable.emoji_181 ));
		emojiMap.put("[衬衫]",R.drawable.emoji_181);
		list.add(new Emotion(182,"小花 ","[小花]", "emoji_182.png",R.drawable.emoji_182 ));
		emojiMap.put("[小花]",R.drawable.emoji_182);
		list.add(new Emotion(183,"郁金香 ","[郁金香]", "emoji_183.png",R.drawable.emoji_183 ));
		emojiMap.put("[郁金香]",R.drawable.emoji_183);
		list.add(new Emotion(184,"向日葵 ","[向日葵]", "emoji_184.png",R.drawable.emoji_184 ));
		emojiMap.put("[向日葵]",R.drawable.emoji_184);
		list.add(new Emotion(185,"鲜花 ","[鲜花]", "emoji_185.png",R.drawable.emoji_185 ));
		emojiMap.put("[鲜花]",R.drawable.emoji_185);
		list.add(new Emotion(186,"椰树 ","[椰树]", "emoji_186.png",R.drawable.emoji_186 ));
		emojiMap.put("[椰树]",R.drawable.emoji_186);
		list.add(new Emotion(187,"仙人掌 ","[仙人掌]", "emoji_187.png",R.drawable.emoji_187 ));
		emojiMap.put("[仙人掌]",R.drawable.emoji_187);
		list.add(new Emotion(188,"气球 ","[气球]", "emoji_188.png",R.drawable.emoji_188 ));
		emojiMap.put("[气球]",R.drawable.emoji_188);
		list.add(new Emotion(189,"炸弹 ","[炸弹]", "emoji_189.png",R.drawable.emoji_189 ));
		emojiMap.put("[炸弹]",R.drawable.emoji_189);
		list.add(new Emotion(190,"喝彩 ","[喝彩]", "emoji_190.png",R.drawable.emoji_190 ));
		emojiMap.put("[喝彩]",R.drawable.emoji_190);
		list.add(new Emotion(191,"剪子 ","[剪子]", "emoji_191.png",R.drawable.emoji_191 ));
		emojiMap.put("[剪子]",R.drawable.emoji_191);
		list.add(new Emotion(192,"蝴蝶结 ","[蝴蝶结]", "emoji_192.png",R.drawable.emoji_192 ));
		emojiMap.put("[蝴蝶结]",R.drawable.emoji_192);
		list.add(new Emotion(193,"机密 ","[机密]", "emoji_193.png",R.drawable.emoji_193 ));
		emojiMap.put("[机密]",R.drawable.emoji_193);
		list.add(new Emotion(194,"铃声 ","[铃声]", "emoji_194.png",R.drawable.emoji_194 ));
		emojiMap.put("[铃声]",R.drawable.emoji_194);
		list.add(new Emotion(195,"女帽 ","[女帽]", "emoji_195.png",R.drawable.emoji_195 ));
		emojiMap.put("[女帽]",R.drawable.emoji_195);
		list.add(new Emotion(196,"裙子 ","[裙子]", "emoji_196.png",R.drawable.emoji_196 ));
		emojiMap.put("[裙子]",R.drawable.emoji_196);
		list.add(new Emotion(197,"理发店 ","[理发店]", "emoji_197.png",R.drawable.emoji_197 ));
		emojiMap.put("[理发店]",R.drawable.emoji_197);
		list.add(new Emotion(198,"和服 ","[和服]", "emoji_198.png",R.drawable.emoji_198 ));
		emojiMap.put("[和服]",R.drawable.emoji_198);
		list.add(new Emotion(199,"比基尼 ","[比基尼]", "emoji_199.png",R.drawable.emoji_199 ));
		emojiMap.put("[比基尼]",R.drawable.emoji_199);
		list.add(new Emotion(200,"拎包 ","[拎包]", "emoji_200.png",R.drawable.emoji_200 ));
		emojiMap.put("[拎包]",R.drawable.emoji_200);
		list.add(new Emotion(201,"拍摄 ","[拍摄]", "emoji_201.png",R.drawable.emoji_201 ));
		emojiMap.put("[拍摄]",R.drawable.emoji_201);
		list.add(new Emotion(202,"铃铛 ","[铃铛]", "emoji_202.png",R.drawable.emoji_202 ));
		emojiMap.put("[铃铛]",R.drawable.emoji_202);
		list.add(new Emotion(203,"音乐 ","[音乐]", "emoji_203.png",R.drawable.emoji_203 ));
		emojiMap.put("[音乐]",R.drawable.emoji_203);
		list.add(new Emotion(204,"心星 ","[心星]", "emoji_204.png",R.drawable.emoji_204 ));
		emojiMap.put("[心星]",R.drawable.emoji_204);
		list.add(new Emotion(205,"粉心 ","[粉心]", "emoji_205.png",R.drawable.emoji_205 ));
		emojiMap.put("[粉心]",R.drawable.emoji_205);
		list.add(new Emotion(206,"丘比特 ","[丘比特]", "emoji_206.png",R.drawable.emoji_206 ));
		emojiMap.put("[丘比特]",R.drawable.emoji_206);
		list.add(new Emotion(207,"吹气 ","[吹气]", "emoji_207.png",R.drawable.emoji_207 ));
		emojiMap.put("[吹气]",R.drawable.emoji_207);
		list.add(new Emotion(208,"口水 ","[口水]", "emoji_208.png",R.drawable.emoji_208 ));
		emojiMap.put("[口水]",R.drawable.emoji_208);
		list.add(new Emotion(209,"对 ","[对]", "emoji_209.png",R.drawable.emoji_209 ));
		emojiMap.put("[对]",R.drawable.emoji_209);
		list.add(new Emotion(210,"错 ","[错]", "emoji_210.png",R.drawable.emoji_210 ));
		emojiMap.put("[错]",R.drawable.emoji_210);
		list.add(new Emotion(211,"绿茶 ","[绿茶]", "emoji_211.png",R.drawable.emoji_211 ));
		emojiMap.put("[绿茶]",R.drawable.emoji_211);
		list.add(new Emotion(212,"面包 ","[面包]", "emoji_212.png",R.drawable.emoji_212 ));
		emojiMap.put("[面包]",R.drawable.emoji_212);
		list.add(new Emotion(213,"面条 ","[面条]", "emoji_213.png",R.drawable.emoji_213 ));
		emojiMap.put("[面条]",R.drawable.emoji_213);
		list.add(new Emotion(214,"咖喱饭 ","[咖喱饭]", "emoji_214.png",R.drawable.emoji_214 ));
		emojiMap.put("[咖喱饭]",R.drawable.emoji_214);
		list.add(new Emotion(215,"饭团 ","[饭团]", "emoji_215.png",R.drawable.emoji_215 ));
		emojiMap.put("[饭团]",R.drawable.emoji_215);
		list.add(new Emotion(216,"麻辣烫 ","[麻辣烫]", "emoji_216.png",R.drawable.emoji_216 ));
		emojiMap.put("[麻辣烫]",R.drawable.emoji_216);
		list.add(new Emotion(217,"寿司 ","[寿司]", "emoji_217.png",R.drawable.emoji_217 ));
		emojiMap.put("[寿司]",R.drawable.emoji_217);
		list.add(new Emotion(218,"苹果 ","[苹果]", "emoji_218.png",R.drawable.emoji_218 ));
		emojiMap.put("[苹果]",R.drawable.emoji_218);
		list.add(new Emotion(219,"橙子 ","[橙子]", "emoji_219.png",R.drawable.emoji_219 ));
		emojiMap.put("[橙子]",R.drawable.emoji_219);
		list.add(new Emotion(220,"草莓 ","[草莓]", "emoji_220.png",R.drawable.emoji_220 ));
		emojiMap.put("[草莓]",R.drawable.emoji_220);
		list.add(new Emotion(221,"西瓜 ","[西瓜]", "emoji_221.png",R.drawable.emoji_221 ));
		emojiMap.put("[西瓜]",R.drawable.emoji_221);
		list.add(new Emotion(222,"柿子 ","[柿子]", "emoji_222.png",R.drawable.emoji_222 ));
		emojiMap.put("[柿子]",R.drawable.emoji_222);
		list.add(new Emotion(223,"眼睛 ","[眼睛]", "emoji_223.png",R.drawable.emoji_223 ));
		emojiMap.put("[眼睛]",R.drawable.emoji_223);
		list.add(new Emotion(224,"好的","[好的]", "emoji_224.png",R.drawable.emoji_224 ));
		emojiMap.put("[好的]",R.drawable.emoji_224);
		return list;
	}

	/**
	 * 查找某个表情
	 * 
	 * @param emotion
	 * @return
	 */
	public static GezitechEntity_I getEmotionByEmotion(String emotion) {
		ArrayList<GezitechEntity_I> list = getDefaultList();
		for (GezitechEntity_I e : list)
			if (((Emotion) e).emotion.equals(emotion))
				return e;
		return null;
	}

	/**
	 * 根据文件名找表情，文件名含有后缀
	 * 
	 * @param filename
	 * @return
	 */
	public static GezitechEntity_I getEmotionByFilename(String filename) {
		ArrayList<GezitechEntity_I> list = getDefaultList();
		for (GezitechEntity_I e : list)
			if (((Emotion) e).filename.equals(filename))
				return e;
		return null;
	}

	public Drawable getEmotionDrawable() {
		if (StringUtil.isEmpty(this.filename))
			return null;
		String file = this.filename;
		if (file.indexOf(".") > -1)
			file = file.substring(0, file.indexOf("."));
		file = "weibo_exp_" + file;
		Context context = GezitechApplication.getContext();
		int iden = context.getResources().getIdentifier(file, "drawable",
				context.getPackageName());
		if (iden == 0)
			return null;
		Drawable draw = context.getResources().getDrawable(iden);
		return draw;
	}

	public Emotion() {
		super();
	}

	public Emotion(long id, String title, String emotion, String filename,
			int drawable) {
		this.id = id;
		this.title = title;
		this.emotion = emotion;
		this.filename = filename;
		this.drawable = drawable;
	}

	/**
	 * 
	 * TODO(解析组装)
	 */
	public ArrayList<ArrayList<GezitechEntity_I>> getParseEmojiList() {
		emojis = getDefaultList();
		ArrayList<ArrayList<GezitechEntity_I>> emojiLists = new ArrayList<ArrayList<GezitechEntity_I>>();
		int pageCount = (int) Math.ceil(emojis.size() / 27 + 0.1);

		for (int i = 0; i < pageCount; i++) {
			emojiLists.add(getData(i));
		}
		return emojiLists;
	}

	/**
	 * 获取分页数据
	 * 
	 * @param page
	 * @return
	 */
	/** 每一页表情的个数 */
	private int pageSize = 27;

	private ArrayList<GezitechEntity_I> getData(int page) {
		int startIndex = page * pageSize;
		int endIndex = startIndex + pageSize;

		if (endIndex > emojis.size()) {
			endIndex = emojis.size();
		}
		// 不这么写，会在viewpager加载中报集合操作异常，我也不知道为什么
		ArrayList<GezitechEntity_I> list = new ArrayList<GezitechEntity_I>();
		list.addAll(emojis.subList(startIndex, endIndex));
		if (list.size() < pageSize) {
			for (int i = list.size(); i < pageSize; i++) {
				Emotion object = new Emotion();
				list.add(object);
			}
		}
		if (list.size() == pageSize) {
			Emotion object = new Emotion();
			object.drawable = R.drawable.face_del_icon_select;
			list.add(object);
		}
		return list;
	}
	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public SpannableString getExpressionString(Context context, String str) {
		SpannableString spannableString = new SpannableString(str);
		// 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
		String zhengze = "\\[[^\\]]+\\]";
		// 通过传入的正则表达式来生成一个pattern
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
		try {
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
			//Log.e("dealExpression", e.getMessage());
			return spannableString;
		}
		return spannableString;
	}
	public SpannableString getExpressionString(Context context, SpannableString spannableString) {
		// 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
		String zhengze = "\\[[^\\]]+\\]";
		// 通过传入的正则表达式来生成一个pattern
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
		try {
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
			//Log.e("dealExpression", e.getMessage());
			return spannableString;
		}
		return spannableString;
	}
	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * 
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws Exception
	 */
	private void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			// 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
			if (matcher.start() < start) {
				continue;
			}
			int value = emojiMap.get(key);
			//if (TextUtils.isEmpty(value)) {
			//	continue;
			//}
			if( value<=0 ) continue;
			/*int resId = context.getResources().getIdentifier(value, "drawable",
					context.getPackageName());*/
			// 通过上面匹配得到的字符串来生成图片资源id
			// Field field=R.drawable.class.getDeclaredField(value);
			// int resId=Integer.parseInt(field.get(null).toString());
			if (value != 0) {
				Bitmap bitmap = BitmapFactory.decodeResource(
						context.getResources(), value);
				bitmap = Bitmap.createScaledBitmap(bitmap, 48, 48, true);
				// 通过图片资源id来得到bitmap，用一个ImageSpan来包装
				ImageSpan imageSpan = new ImageSpan(context, bitmap);
				// 计算该图片名字的长度，也就是要替换的字符串的长度
				int end = matcher.start() + key.length();
				// 将该图片替换字符串中规定的位置中
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				if (end < spannableString.length()) {
					// 如果整个字符串还未验证完，则继续。。
					dealExpression(context, spannableString, patten, end);
				}
				break;
			}
		}
	}

}
