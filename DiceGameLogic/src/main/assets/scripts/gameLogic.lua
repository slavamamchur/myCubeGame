local sysUtilsWrapper, gl3DScene, restApi = ...

local ROLLING_DICE_SOUND = 'rolling_dice.mp3'
local SKY_BOX_CUBE_MAP_OBJECT = 'SKY_BOX_CUBE_MAP_OBJECT'
local TERRAIN_MESH_OBJECT = 'TERRAIN_MESH_OBJECT'
local DICE_MESH_OBJECT = 'DICE_MESH_OBJECT_1'
--local javaDiceObjName = luajava.newInstance('java.lang.String', DICE_MESH_OBJECT)

local GAME_DICE_HALF_SIZE = 0.15
local BOX_SHAPE_TYPE = 1

local ON_PLAY_TURN_ANIMATION_END = 'rollDice'
local ON_STOP_MOVING_ANIMATION_END = 'playerNextMove'

onRollingObjectStart = function(gameObject)
    if gameObject == gl3DScene:getObject(DICE_MESH_OBJECT) then
        sysUtilsWrapper:iPlaySound(ROLLING_DICE_SOUND)
    end
end

onRollingObjectStop = function(gameObject)
    sysUtilsWrapper:iStopSound()
end

onMovingObjectStop = function(gameObject, gameInstance)
    if not (gameInstance == nil) then
        gameInstance:setStepsToGo(gameObject:getTopFaceDiceValue())  --todo: -> getTopFaceDiceValue(gameObject)
        restApi:showTurnInfo(gameInstance)

        gl3DScene:restorePrevViewMode()

        gameObject:hideObject()

        gl3DScene:setZoomCameraAnimation(gl3DScene:createZoomCameraAnimation(2.0))
        gl3DScene:getZoomCameraAnimation():startAnimation(nil, ON_STOP_MOVING_ANIMATION_END, {gameInstance})
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
    dice:setPWorldTransform(generateDiceInitialTransform())
    dice:get_body():setLinearVelocity(generateForceVector())

    gl3DScene:getPhysicalWorldObject():addRigidBody(dice:get_body())
end

playerNextMove = function(gameInstance)
    restApi:moveGameInstance(gameInstance)
end

function getTopFaceDiceValue(dice)
    --todo:
end

function generateForceVector()
    math.randomseed(os.time())

    local fxz = 3.5 + math.random(0, 1) * 1.0 --2
    local fy = fxz * 3.0 / 4.0
    local fVector = {0.0, fy, -fxz, 1.0}

    local transform = sysUtilsWrapper:createTransform()
    transform:rotY(math.rad(45.0 - math.random(0, 90) * 1.0))

    return sysUtilsWrapper:mulMV(transform, fVector)
end

function generateDiceInitialTransform()
    math.randomseed(os.time())

    local transformer = sysUtilsWrapper:createTransform()
    local transformingObject = sysUtilsWrapper:createTransform()

    transformingObject:setIdentity();
    transformer:setIdentity();

    transformer:setTranslation(sysUtilsWrapper:createVector3f(0.0, 0.5, 2.5))
    transformingObject:mul(transformer)

    transformer:rotX(math.rad(math.random(0, 3) * 90.0)) --4
    transformingObject:mul(transformer)

    transformer:rotY(math.rad(math.random(0, 3) * 90.0))
    transformingObject:mul(transformer)

    transformer:rotZ(math.rad(math.random(0, 3) * 90.0))
    transformingObject:mul(transformer)

    return transformingObject
end

function make_array(dataClass, values)
    local arrayClass = luajava.bindClass("java.lang.reflect.Array")
    if(arrayClass == nil) then
        print("Can't get array class")
        return nil
    end

    local newTypedArray = arrayClass:newInstance(dataClass, #values)
    if(newTypedArray == nil) then
        print("Can't get array class")
        return nil
    end

    for i=1,#values do
        arrayClass:set(newTypedArray, i-1, values[i])
    end

    return newTypedArray
end
