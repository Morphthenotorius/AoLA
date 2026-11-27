package AoLA.game.Utilities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CameraShake {
    private float shakeTimer = 0f;
    private float shakeIntensity = 0f;
    private final Vector3 originalPosition = new Vector3();
    private boolean isShaking = false;

    public void shake (float intensity, float duration){
        this.shakeIntensity = intensity;
        this.shakeTimer = duration;
    }

    public void update(OrthographicCamera camera, float delta){
        if(shakeTimer > 0f){
            if(!isShaking){
                originalPosition.set(camera.position);
                isShaking = true;
            }

            shakeTimer -=delta;
            float offsetX = MathUtils.random(-shakeIntensity, shakeIntensity);
            float offsetY = MathUtils.random(-shakeIntensity,shakeIntensity);

            camera.position.set(originalPosition.x + offsetX, originalPosition.y + offsetY, originalPosition.z);
            camera.update();
        }

        else if(isShaking){
            camera.position.set(originalPosition);
            camera.update();
            isShaking = false;
        }
    }
}
