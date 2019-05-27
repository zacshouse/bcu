package util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class EPart extends ImgCore implements Comparable<EPart> {

	protected EAnimU ea;
	private final AnimU a;
	private final int[] args;
	private final EPart[] ent;
	private EPart fa;
	private int hf, vf;
	private int id, img, gsca;
	private final MaModel model;
	private P pos, piv, sca;

	private int z, angle, opacity, glow, extend;

	protected EPart(MaModel mm, AnimU aa, int[] part, EPart[] ents) {
		model = mm;
		a = aa;
		args = part;
		ent = ents;
		setValue();
	}

	@Override
	public int compareTo(EPart o) {
		return z > o.z ? 1 : (z == o.z ? 0 : -1);
	}

	protected void alter(int m, int v) {
		if (m == 0)
			if (v < ent.length)
				fa = ent[v];
			else
				fa = ent[0];
		else if (m == 1)
			id = v;
		else if (m == 2)
			img = v;
		else if (m == 3) {
			z = v;
			ea.order.sort(null);
		} else if (m == 4)
			pos.x = args[4] + v;
		else if (m == 5)
			pos.y = args[5] + v;
		else if (m == 6)
			piv.x = args[6] + v;
		else if (m == 7)
			piv.y = args[7] + v;
		else if (m == 8)
			gsca = v;
		else if (m == 9)
			sca.x = args[8] * v / model.ints[0];
		else if (m == 10)
			sca.y = args[9] * v / model.ints[0];
		else if (m == 11)
			angle = args[10] + v;
		else if (m == 12)
			opacity = v * args[11] / model.ints[2];
		else if (m == 13)
			hf = v == 0 ? 1 : -1;
		else if (m == 14)
			vf = v == 0 ? 1 : -1;
		else if (m == 50)
			extend = v;

	}

	protected void drawPart(Graphics2D g, P base) {
		if (img < 0 || id < 0 || opa() < deadOpa * 0.01 + 1e-5 || a.parts(img) == null)
			return;
		AffineTransform at = g.getTransform();
		transform(g, base);
		BufferedImage bimg = a.parts(img);
		int w = bimg.getWidth();
		int h = bimg.getHeight();
		P tpiv = piv.copy().times(getSize()).times(base);
		P sc = new P(w, h).times(getSize()).times(base);
		drawImg(g, bimg, tpiv, sc, opa(), glow == 1, 1.0 * extend / model.ints[0]);
		g.setTransform(at);
	}

	protected void setValue() {
		if (args[0] >= ent.length)
			args[0] = 0;
		fa = args[0] <= -1 ? null : ent[args[0]];
		id = args[1];
		img = args[2];
		z = args[3];
		pos = new P(args[4], args[5]);
		piv = new P(args[6], args[7]);
		sca = new P(args[8], args[9]);
		angle = args[10];
		opacity = args[11];
		glow = args[12];
		extend = args[13];
		gsca = model.ints[0];
		hf = vf = 1;
	}

	private P getSize() {
		double mi = 1.0 / model.ints[0];
		if (fa == null)
			return sca.copy().times(gsca * mi * mi);
		return fa.getSize().times(sca).times(gsca * mi * mi);
	}

	private double opa() {
		if (opacity == 0)
			return 0;
		if (fa != null)
			return fa.opa() * opacity / model.ints[2];
		return 1.0 * opacity / model.ints[2];
	}

	private void transform(Graphics2D g, P sizer) {
		P siz = sizer;
		if (fa != null) {
			fa.transform(g, sizer);
			siz = fa.getSize().times(sizer);
		}
		P tpos = pos.copy().times(siz);
		if (ent[0] != this) {
			g.translate(tpos.x, tpos.y);
			g.scale(hf, vf);
		} else {
			if (model.confs.length > 0) {
				int[] data = model.confs[0];
				P shi = new P(data[2], data[3]).times(getSize());
				P p3 = shi.times(sizer);
				g.translate(-p3.x, -p3.y);
			}
			P p = piv.copy().times(getSize()).times(sizer);
			g.translate(p.x, p.y);
		}
		if (angle != 0)
			g.rotate(Math.PI * 2 * angle / model.ints[1]);
	}

}