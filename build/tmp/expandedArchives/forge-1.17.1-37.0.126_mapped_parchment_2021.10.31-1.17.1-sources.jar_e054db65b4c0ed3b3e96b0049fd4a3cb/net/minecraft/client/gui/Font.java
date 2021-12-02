package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import net.minecraft.util.StringDecomposer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Font {
   private static final float EFFECT_DEPTH = 0.01F;
   private static final Vector3f SHADOW_OFFSET = new Vector3f(0.0F, 0.0F, 0.03F);
   public final int lineHeight = 9;
   public final Random random = new Random();
   private final Function<ResourceLocation, FontSet> fonts;
   private final StringSplitter splitter;

   public Font(Function<ResourceLocation, FontSet> p_92717_) {
      this.fonts = p_92717_;
      this.splitter = new StringSplitter((p_92722_, p_92723_) -> {
         return this.getFontSet(p_92723_.getFont()).getGlyphInfo(p_92722_).getAdvance(p_92723_.isBold());
      });
   }

   FontSet getFontSet(ResourceLocation pFontLocation) {
      return this.fonts.apply(pFontLocation);
   }

   public int drawShadow(PoseStack pMatrixStack, String pText, float pX, float pY, int pColor) {
      return this.drawInternal(pText, pX, pY, pColor, pMatrixStack.last().pose(), true, this.isBidirectional());
   }

   public int drawShadow(PoseStack pMatrixStack, String pText, float pX, float pY, int pColor, boolean pTransparency) {
      return this.drawInternal(pText, pX, pY, pColor, pMatrixStack.last().pose(), true, pTransparency);
   }

   public int draw(PoseStack pMatrixStack, String pText, float pX, float pY, int pColor) {
      return this.drawInternal(pText, pX, pY, pColor, pMatrixStack.last().pose(), false, this.isBidirectional());
   }

   public int drawShadow(PoseStack pMatrixStack, FormattedCharSequence pText, float pX, float pY, int pColor) {
      return this.drawInternal(pText, pX, pY, pColor, pMatrixStack.last().pose(), true);
   }

   public int drawShadow(PoseStack pMatrixStack, Component pText, float pX, float pY, int pColor) {
      return this.drawInternal(pText.getVisualOrderText(), pX, pY, pColor, pMatrixStack.last().pose(), true);
   }

   public int draw(PoseStack p_92878_, FormattedCharSequence p_92879_, float p_92880_, float p_92881_, int p_92882_) {
      return this.drawInternal(p_92879_, p_92880_, p_92881_, p_92882_, p_92878_.last().pose(), false);
   }

   public int draw(PoseStack pMatrixStack, Component pText, float pX, float pY, int pColor) {
      return this.drawInternal(pText.getVisualOrderText(), pX, pY, pColor, pMatrixStack.last().pose(), false);
   }

   /**
    * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
    */
   public String bidirectionalShaping(String pText) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(pText), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException arabicshapingexception) {
         return pText;
      }
   }

   private int drawInternal(String pText, float pX, float pY, int pColor, Matrix4f pMatrix, boolean pDropShadow, boolean pTransparency) {
      if (pText == null) {
         return 0;
      } else {
         MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         int i = this.drawInBatch(pText, pX, pY, pColor, pDropShadow, pMatrix, multibuffersource$buffersource, false, 0, 15728880, pTransparency);
         multibuffersource$buffersource.endBatch();
         return i;
      }
   }

   private int drawInternal(FormattedCharSequence pReorderingProcessor, float pX, float pY, int pColor, Matrix4f pMatrix, boolean pDrawShadow) {
      MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      int i = this.drawInBatch(pReorderingProcessor, pX, pY, pColor, pDrawShadow, pMatrix, multibuffersource$buffersource, false, 0, 15728880);
      multibuffersource$buffersource.endBatch();
      return i;
   }

   public int drawInBatch(String pText, float pX, float pY, int pColor, boolean pDropShadow, Matrix4f pMatrix, MultiBufferSource pBuffer, boolean pTransparent, int pColorBackground, int pPackedLight) {
      return this.drawInBatch(pText, pX, pY, pColor, pDropShadow, pMatrix, pBuffer, pTransparent, pColorBackground, pPackedLight, this.isBidirectional());
   }

   public int drawInBatch(String pText, float pX, float pY, int pColor, boolean pDropShadow, Matrix4f pMatrix, MultiBufferSource pBuffer, boolean pTransparency, int pColorBackground, int pPackedLight, boolean pBidiFlag) {
      return this.drawInternal(pText, pX, pY, pColor, pDropShadow, pMatrix, pBuffer, pTransparency, pColorBackground, pPackedLight, pBidiFlag);
   }

   public int drawInBatch(Component p_92842_, float p_92843_, float p_92844_, int p_92845_, boolean p_92846_, Matrix4f p_92847_, MultiBufferSource p_92848_, boolean p_92849_, int p_92850_, int p_92851_) {
      return this.drawInBatch(p_92842_.getVisualOrderText(), p_92843_, p_92844_, p_92845_, p_92846_, p_92847_, p_92848_, p_92849_, p_92850_, p_92851_);
   }

   public int drawInBatch(FormattedCharSequence pProcessor, float pX, float pY, int pColor, boolean pDropShadow, Matrix4f pMatrix, MultiBufferSource pBuffer, boolean pTransparent, int pColorBackground, int pPackedLight) {
      return this.drawInternal(pProcessor, pX, pY, pColor, pDropShadow, pMatrix, pBuffer, pTransparent, pColorBackground, pPackedLight);
   }

   public void drawInBatch8xOutline(FormattedCharSequence p_168646_, float p_168647_, float p_168648_, int p_168649_, int p_168650_, Matrix4f p_168651_, MultiBufferSource p_168652_, int p_168653_) {
      int i = adjustColor(p_168650_);
      Font.StringRenderOutput font$stringrenderoutput = new Font.StringRenderOutput(p_168652_, 0.0F, 0.0F, i, false, p_168651_, Font.DisplayMode.NORMAL, p_168653_);

      for(int j = -1; j <= 1; ++j) {
         for(int k = -1; k <= 1; ++k) {
            if (j != 0 || k != 0) {
               float[] afloat = new float[]{p_168647_};
               int l = j;
               int i1 = k;
               p_168646_.accept((p_168661_, p_168662_, p_168663_) -> {
                  boolean flag = p_168662_.isBold();
                  FontSet fontset = this.getFontSet(p_168662_.getFont());
                  GlyphInfo glyphinfo = fontset.getGlyphInfo(p_168663_);
                  font$stringrenderoutput.x = afloat[0] + (float)l * glyphinfo.getShadowOffset();
                  font$stringrenderoutput.y = p_168648_ + (float)i1 * glyphinfo.getShadowOffset();
                  afloat[0] += glyphinfo.getAdvance(flag);
                  return font$stringrenderoutput.accept(p_168661_, p_168662_.withColor(i), p_168663_);
               });
            }
         }
      }

      Font.StringRenderOutput font$stringrenderoutput1 = new Font.StringRenderOutput(p_168652_, p_168647_, p_168648_, adjustColor(p_168649_), false, p_168651_, Font.DisplayMode.POLYGON_OFFSET, p_168653_);
      p_168646_.accept(font$stringrenderoutput1);
      font$stringrenderoutput1.finish(0, p_168647_);
   }

   private static int adjustColor(int pColor) {
      return (pColor & -67108864) == 0 ? pColor | -16777216 : pColor;
   }

   private int drawInternal(String pText, float pX, float pY, int pColor, boolean pDropShadow, Matrix4f pMatrix, MultiBufferSource pBuffer, boolean pTransparent, int pColorBackground, int pPackedLight, boolean pBidiFlag) {
      if (pBidiFlag) {
         pText = this.bidirectionalShaping(pText);
      }

      pColor = adjustColor(pColor);
      Matrix4f matrix4f = pMatrix.copy();
      if (pDropShadow) {
         this.renderText(pText, pX, pY, pColor, true, pMatrix, pBuffer, pTransparent, pColorBackground, pPackedLight);
         matrix4f.translate(SHADOW_OFFSET);
      }

      pX = this.renderText(pText, pX, pY, pColor, false, matrix4f, pBuffer, pTransparent, pColorBackground, pPackedLight);
      return (int)pX + (pDropShadow ? 1 : 0);
   }

   private int drawInternal(FormattedCharSequence pProcessor, float pX, float pY, int pColor, boolean pDrawShadow, Matrix4f pMatrix, MultiBufferSource pBuffer, boolean pTransparent, int pColorBackground, int pPackedLight) {
      pColor = adjustColor(pColor);
      Matrix4f matrix4f = pMatrix.copy();
      if (pDrawShadow) {
         this.renderText(pProcessor, pX, pY, pColor, true, pMatrix, pBuffer, pTransparent, pColorBackground, pPackedLight);
         matrix4f.translate(SHADOW_OFFSET);
      }

      pX = this.renderText(pProcessor, pX, pY, pColor, false, matrix4f, pBuffer, pTransparent, pColorBackground, pPackedLight);
      return (int)pX + (pDrawShadow ? 1 : 0);
   }

   private float renderText(String pText, float pX, float pY, int pColor, boolean pIsShadow, Matrix4f pMatrix, MultiBufferSource pBuffer, boolean pIsTransparent, int pColorBackground, int pPackedLight) {
      Font.StringRenderOutput font$stringrenderoutput = new Font.StringRenderOutput(pBuffer, pX, pY, pColor, pIsShadow, pMatrix, pIsTransparent, pPackedLight);
      StringDecomposer.iterateFormatted(pText, Style.EMPTY, font$stringrenderoutput);
      return font$stringrenderoutput.finish(pColorBackground, pX);
   }

   private float renderText(FormattedCharSequence p_92927_, float p_92928_, float p_92929_, int p_92930_, boolean p_92931_, Matrix4f p_92932_, MultiBufferSource p_92933_, boolean p_92934_, int p_92935_, int p_92936_) {
      Font.StringRenderOutput font$stringrenderoutput = new Font.StringRenderOutput(p_92933_, p_92928_, p_92929_, p_92930_, p_92931_, p_92932_, p_92934_, p_92936_);
      p_92927_.accept(font$stringrenderoutput);
      return font$stringrenderoutput.finish(p_92935_, p_92928_);
   }

   void renderChar(BakedGlyph pGlyph, boolean pBold, boolean pItalic, float pBoldOffset, float pX, float pY, Matrix4f pMatrix, VertexConsumer pBuffer, float pRed, float pGreen, float pBlue, float pAlpha, int pPackedLight) {
      pGlyph.render(pItalic, pX, pY, pMatrix, pBuffer, pRed, pGreen, pBlue, pAlpha, pPackedLight);
      if (pBold) {
         pGlyph.render(pItalic, pX + pBoldOffset, pY, pMatrix, pBuffer, pRed, pGreen, pBlue, pAlpha, pPackedLight);
      }

   }

   /**
    * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
    */
   public int width(String pText) {
      return Mth.ceil(this.splitter.stringWidth(pText));
   }

   public int width(FormattedText pProperties) {
      return Mth.ceil(this.splitter.stringWidth(pProperties));
   }

   public int width(FormattedCharSequence p_92725_) {
      return Mth.ceil(this.splitter.stringWidth(p_92725_));
   }

   public String plainSubstrByWidth(String p_92838_, int p_92839_, boolean p_92840_) {
      return p_92840_ ? this.splitter.plainTailByWidth(p_92838_, p_92839_, Style.EMPTY) : this.splitter.plainHeadByWidth(p_92838_, p_92839_, Style.EMPTY);
   }

   public String plainSubstrByWidth(String pText, int pMaxLength) {
      return this.splitter.plainHeadByWidth(pText, pMaxLength, Style.EMPTY);
   }

   public FormattedText substrByWidth(FormattedText p_92855_, int p_92856_) {
      return this.splitter.headByWidth(p_92855_, p_92856_, Style.EMPTY);
   }

   public void drawWordWrap(FormattedText p_92858_, int p_92859_, int p_92860_, int p_92861_, int p_92862_) {
      Matrix4f matrix4f = Transformation.identity().getMatrix();

      for(FormattedCharSequence formattedcharsequence : this.split(p_92858_, p_92861_)) {
         this.drawInternal(formattedcharsequence, (float)p_92859_, (float)p_92860_, p_92862_, matrix4f, false);
         p_92860_ += 9;
      }

   }

   /**
    * Returns the height (in pixels) of the given string if it is wordwrapped to the given max width.
    */
   public int wordWrapHeight(String pStr, int pMaxLength) {
      return 9 * this.splitter.splitLines(pStr, pMaxLength, Style.EMPTY).size();
   }

   public List<FormattedCharSequence> split(FormattedText p_92924_, int p_92925_) {
      return Language.getInstance().getVisualOrder(this.splitter.splitLines(p_92924_, p_92925_, Style.EMPTY));
   }

   /**
    * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be run before rendering any string
    */
   public boolean isBidirectional() {
      return Language.getInstance().isDefaultRightToLeft();
   }

   public StringSplitter getSplitter() {
      return this.splitter;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum DisplayMode {
      NORMAL,
      SEE_THROUGH,
      POLYGON_OFFSET;
   }

   @OnlyIn(Dist.CLIENT)
   class StringRenderOutput implements FormattedCharSink {
      final MultiBufferSource bufferSource;
      private final boolean dropShadow;
      private final float dimFactor;
      private final float r;
      private final float g;
      private final float b;
      private final float a;
      private final Matrix4f pose;
      private final Font.DisplayMode mode;
      private final int packedLightCoords;
      float x;
      float y;
      @Nullable
      private List<BakedGlyph.Effect> effects;

      private void addEffect(BakedGlyph.Effect pEffect) {
         if (this.effects == null) {
            this.effects = Lists.newArrayList();
         }

         this.effects.add(pEffect);
      }

      public StringRenderOutput(MultiBufferSource p_92953_, float p_92954_, float p_92955_, int p_92956_, boolean p_92957_, Matrix4f p_92958_, boolean p_92959_, int p_92960_) {
         this(p_92953_, p_92954_, p_92955_, p_92956_, p_92957_, p_92958_, p_92959_ ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, p_92960_);
      }

      public StringRenderOutput(MultiBufferSource p_181365_, float p_181366_, float p_181367_, int p_181368_, boolean p_181369_, Matrix4f p_181370_, Font.DisplayMode p_181371_, int p_181372_) {
         this.bufferSource = p_181365_;
         this.x = p_181366_;
         this.y = p_181367_;
         this.dropShadow = p_181369_;
         this.dimFactor = p_181369_ ? 0.25F : 1.0F;
         this.r = (float)(p_181368_ >> 16 & 255) / 255.0F * this.dimFactor;
         this.g = (float)(p_181368_ >> 8 & 255) / 255.0F * this.dimFactor;
         this.b = (float)(p_181368_ & 255) / 255.0F * this.dimFactor;
         this.a = (float)(p_181368_ >> 24 & 255) / 255.0F;
         this.pose = p_181370_;
         this.mode = p_181371_;
         this.packedLightCoords = p_181372_;
      }

      public boolean accept(int p_92967_, Style p_92968_, int p_92969_) {
         FontSet fontset = Font.this.getFontSet(p_92968_.getFont());
         GlyphInfo glyphinfo = fontset.getGlyphInfo(p_92969_);
         BakedGlyph bakedglyph = p_92968_.isObfuscated() && p_92969_ != 32 ? fontset.getRandomGlyph(glyphinfo) : fontset.getGlyph(p_92969_);
         boolean flag = p_92968_.isBold();
         float f3 = this.a;
         TextColor textcolor = p_92968_.getColor();
         float f;
         float f1;
         float f2;
         if (textcolor != null) {
            int i = textcolor.getValue();
            f = (float)(i >> 16 & 255) / 255.0F * this.dimFactor;
            f1 = (float)(i >> 8 & 255) / 255.0F * this.dimFactor;
            f2 = (float)(i & 255) / 255.0F * this.dimFactor;
         } else {
            f = this.r;
            f1 = this.g;
            f2 = this.b;
         }

         if (!(bakedglyph instanceof EmptyGlyph)) {
            float f5 = flag ? glyphinfo.getBoldOffset() : 0.0F;
            float f4 = this.dropShadow ? glyphinfo.getShadowOffset() : 0.0F;
            VertexConsumer vertexconsumer = this.bufferSource.getBuffer(bakedglyph.renderType(this.mode));
            Font.this.renderChar(bakedglyph, flag, p_92968_.isItalic(), f5, this.x + f4, this.y + f4, this.pose, vertexconsumer, f, f1, f2, f3, this.packedLightCoords);
         }

         float f6 = glyphinfo.getAdvance(flag);
         float f7 = this.dropShadow ? 1.0F : 0.0F;
         if (p_92968_.isStrikethrough()) {
            this.addEffect(new BakedGlyph.Effect(this.x + f7 - 1.0F, this.y + f7 + 4.5F, this.x + f7 + f6, this.y + f7 + 4.5F - 1.0F, 0.01F, f, f1, f2, f3));
         }

         if (p_92968_.isUnderlined()) {
            this.addEffect(new BakedGlyph.Effect(this.x + f7 - 1.0F, this.y + f7 + 9.0F, this.x + f7 + f6, this.y + f7 + 9.0F - 1.0F, 0.01F, f, f1, f2, f3));
         }

         this.x += f6;
         return true;
      }

      public float finish(int p_92962_, float p_92963_) {
         if (p_92962_ != 0) {
            float f = (float)(p_92962_ >> 24 & 255) / 255.0F;
            float f1 = (float)(p_92962_ >> 16 & 255) / 255.0F;
            float f2 = (float)(p_92962_ >> 8 & 255) / 255.0F;
            float f3 = (float)(p_92962_ & 255) / 255.0F;
            this.addEffect(new BakedGlyph.Effect(p_92963_ - 1.0F, this.y + 9.0F, this.x + 1.0F, this.y - 1.0F, 0.01F, f1, f2, f3, f));
         }

         if (this.effects != null) {
            BakedGlyph bakedglyph = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            VertexConsumer vertexconsumer = this.bufferSource.getBuffer(bakedglyph.renderType(this.mode));

            for(BakedGlyph.Effect bakedglyph$effect : this.effects) {
               bakedglyph.renderEffect(bakedglyph$effect, this.pose, vertexconsumer, this.packedLightCoords);
            }
         }

         return this.x;
      }
   }
}