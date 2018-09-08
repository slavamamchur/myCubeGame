local sysUtilsWrapper, gl3DScene = ...

local ROLLING_DICE_SOUND = 'rolling_dice.mp3'
local SKY_BOX_CUBE_MAP_OBJECT = 'SKY_BOX_CUBE_MAP_OBJECT'
local TERRAIN_MESH_OBJECT = 'TERRAIN_MESH_OBJECT'
local DICE_MESH_OBJECT = 'DICE_MESH_OBJECT_1'

local ON_PLAY_TURN_ANIMATION_END = 'rollDice'
local ON_STOP_MOVING_ANIMATION_END = 'playerNextMove'

--local javaDiceObjName = luajava.newInstance('java.lang.String', DICE_MESH_OBJECT)
--local objectType = luajava.bindClass('java.lang.String$GLObjectType')

onRollingObjectStart = function(gameObject)
    if gameObject == gl3DScene:getObject(DICE_MESH_OBJECT) then
        sysUtilsWrapper:iPlaySound(ROLLING_DICE_SOUND)
    end
end

onRollingObjectStop = function(gameObject)
    sysUtilsWrapper:iStopSound()
end

onMovingObjectStop = function(gameObject, gameInstance, restApi)
    if not (gameInstance == nil) then
        gameInstance:setStepsToGo(gameObject:getTopFaceDiceValue())
        restApi:showTurnInfo(gameInstance)

        gl3DScene:restorePrevViewMode()

        gameObject:hideObject()


        gl3DScene:setZoomCameraAnimation(gl3DScene:createZoomCameraAnimation(2.0))
        gl3DScene:getZoomCameraAnimation():startAnimation(nil, ON_STOP_MOVING_ANIMATION_END, {gameInstance, restApi})
    end
end

beforeDrawFrame = function(frametime)
    local skyBox = gl3DScene:getObject(SKY_BOX_CUBE_MAP_OBJECT)

    skyBox:calcRotationAngle(frametime)
    gl3DScene:getObject(TERRAIN_MESH_OBJECT):getProgram():setSkyBoxRotationAngle(-skyBox:getRotationAngle())
end

onPlayTurn = function()
    gl3DScene:setZoomCameraAnimation(gl3DScene:createZoomCameraAnimation(0.5))
    gl3DScene:getZoomCameraAnimation():startAnimation(nil, ON_PLAY_TURN_ANIMATION_END, {})
end

rollDice = function()
    local dice = gl3DScene:getObject(DICE_MESH_OBJECT)

    gl3DScene:switrchTo2DMode()

    dice:createRigidBody()
    dice:generateInitialTransform()
    dice:generateForceVector()

    gl3DScene:getPhysicalWorldObject():addRigidBody(dice:get_body())
end

playerNextMove = function(gameInstance, restApi)
    restApi:moveGameInstance(gameInstance);
end
