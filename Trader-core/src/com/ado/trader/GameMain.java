package com.ado.trader;

import com.ado.trader.screens.MainMenu;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class GameMain extends Game {
	public static final String LOG = "Debug log: ";
	public static final String VERSION = "0.0.0.01";
	
	@Override
	public void create() {		
		setScreen(new MainMenu(this));
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	
	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
	
	static public ShaderProgram createSpriteShader () {
	   String vertexShader = "#version 130\n"
	      + "in vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
	      + "in vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
	      + "in vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
	      + "uniform mat4 u_projTrans;\n" //
	      + "uniform mat4 u_projModelView;\n" //
	      + "out vec4 v_color;\n" //
	      + "out vec2 v_texCoords;\n" //
	      + "\n" //
	      + "void main()\n" //
	      + "{\n" //
	      + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
	      + "   v_color.a = v_color.a * (255.0/254.0);\n" //
	      + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
	      + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
	      + "}\n";
	   String fragmentShader = "#version 130\n"
	      + "#ifdef GL_ES\n" //
	      + "#define LOWP lowp\n" //
	      + "precision mediump float;\n" //
	      + "#else\n" //
	      + "#define LOWP \n" //
	      + "#endif\n" //
	      + "in LOWP vec4 v_color;\n" //
	      + "in vec2 v_texCoords;\n" //
	      + "out vec4 fragColor;\n" //
	      + "uniform sampler2D u_texture;\n" //
	      + "uniform sampler2D u_projModelView;\n" //
	      + "void main()\n"//
	      + "{\n" //
	      + "  fragColor = v_color * texture(u_texture, v_texCoords);\n" //
	      + "}";

//		   ShaderProgram.pedantic = false;
	   ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
	   if (shader.isCompiled() == false) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
	   return shader;
	}
	
	static public ShaderProgram createShapeShader () {
	   String vertexShader = "#version 130\n"
		  + "attribute vec4 a_position;\n" //
	      + "attribute vec4 a_color;\n" //
	      + "uniform mat4 u_projModelView;\n" //
	      + "varying vec4 v_col;\n" //
	      + "\n" //
	      + "void main()\n" //
	      + "{\n" //
	      + "   gl_Position = u_projModelView * a_position;\n" //
	      + "   v_col = a_color;\n" //
	      + "   gl_PointSize = 1.0;\n" //
	      + "}\n";
	   String fragmentShader = "#version 130\n" 
		  + "#ifdef GL_ES\n" //
	      + "precision mediump float;\n" //
	      + "#endif\n" //
	      + "varying vec4 v_col;\n" //
	      + "void main()\n"//
	      + "{\n" //
	      + "  gl_FragColor = v_col;\n" //
	      + "}";

//			   ShaderProgram.pedantic = false;
	   ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
	   if (shader.isCompiled() == false) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
	   return shader;
	}
}
