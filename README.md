# k Minimum Bounding Rectangle Problem

**Problem Definition**: x, y 축으로 구성되는 2차원 공간에서 N개의 점(data point)들이 주어졌을 때, k개의 점을 포함하는, 가장 작은 사각형을 찾는 것을 목표로 함.
이때, 문제의 부수적인 조건은 다음과 같음
1. N >> k. N은 10,000 이상(10,000,000개 이상이 될 수도 있음), k는 5~50 정도의 범위로 함(조금 더 크게해도 되지만, 너무 크게하는 건 고려하지 않음)
2. 사각형은 axis-parallel rectangle로 한정한다. (각 변이 x, y축에 평행한 직사각형)
2. 사각형의 크기 measure는 사각형의 둘레로 정의한다.

## Project structure
- `edu.hanyang.kmbr`
- `edu.hanyang.gridshift`

### `edu.hanyang.kmbr`
kMBR 프로젝트가 구현된 패키지

### `edu.hanyang.gridshift`
**관련연구 구현.** GridShift [1]라는 mode seeking 알고리즘을 구현한 패키지

## Execution
본 프로젝트에는 다음과 같이 여러가지 실행 모드가 있음
- kMBR 실행모드
  - Interactive mode
  - Simulation test mode (visualization mode)
  - Speed test mode
  - Cache test mode
  - Time test mode
  - Citibike dataset test mode
- GridShift 실행모드
  - GridShift test mode

### Common prerequisite
모든 실행 모드는 다음과 같은 선행 작업이 필요함
1. Intellij (by JetBrain) 설치 (이클립스도 상관없으나, intellij 권장)
2. Intellij 메뉴에서 Open project로 본 레포지토리 프로젝트 폴더 오픈(레포지토리 폴더자체를 선택해서 열어야 함, "research-2024-kmbr" 폴더 선택)

### kMBR interactive mode
1. Intellij project explorer에서 `src/main/java/edu/hanyang/kmbr/KMBRApp.java` 파일 마우스 오른쪽 버튼 클릭
2. 'Run KMBRApp.main()' 클릭

**Interactive mode 사용법:**
- `generate NUM_OF_POINTS NUM_OF_CLUSTERS`: `NUM_OF_POINTS`개의 데이터를 생성하는데, `NUM_OF_CLUSTERS`개의 군집을 이루도록 생성 **(이전 데이터포인트 모두 제거)**
- `insert NUM_OF_POINTS`: `NUM_OF_POINTS`개의 데이터를 추가 생성 (기생성되어있던 데이터들의 클러스터 개수에 맞추어 생성)
- `insert one X Y`: `(X, Y)` 좌표를 갖는 점 1개 추가생성
- `move NUMBER_OF_MIN_MOVE NUMBER_OF_MAX_MOVE`: 랜덤으로 최소 `NUMBER_OF_MIN_MOVE`개 이상 `NUMBER_OF_MAX_MOVE`개 이하의 데이터포인트 이동
- `compute`: kMBR 계산(kMBR 계산 전 cache bit 업데이트 과정 포함)
- `update`: cache bit 업데이트. `compute` 명령에 포함되어 있음. kMBR 계산없이 cache bit 만 업데이트하고 싶은 경우 사용
- `print tree`: binary tree 구조 출력
- `print height`: binary tree 각 노드의 height 출력
- `print cache`: 캐시된 kMBR 정보출력
- `print cachebits`: 모든 노드에 대한 cache bit 출력
- `print dirty`: 모든 leaf node에 대한 dirty probability 출력
- `clear`: 모든 데이터 제거
- `exit`: 종료

Interactive mode 사용법(예시):
```bash
INPUT> generate 10000 10  # 기존의 모든 데이터포인트를 지우고 10000개의 데이터를 생성하는데, 10개의 군집을 이루도록 생성함
INPUT> print tree # 데이터 생성 이후 binary tree structure 출력
INPUT> insert 10000 # 기존의 데이터를 유지한 채, 10000개의 데이터포인트 추가생성. 10개의 군집을 이루도록 생성하는데, 이전에 생성된 10개의 군집이 아닌, 새로운 10개의 군집으로 생성됨
INPUT> print cache # binary tree에서 어느 노드가 캐싱되었는지 프린트 (아직 kMBR계산안했으니 아무것도 안나옴)
INPUT> compute # kMBR 계산 명령, 이때, cache bit가 먼저 업데이트되고 kMBR이 계산됨, 계산결과 kMBR 크기 및 계산시간이 출력됨. 첫 계산이기 때문에 계산시간은 캐싱이 반영되지 않은 계산시간임
INPUT> compute # kMBR 계산 명령, kMBR 크기와 캐싱이 반영된 계산시간 출력
INPUT> clear # 모든 데이터포인트 제거 (binary tree 제거)
INPUT> exit # 종료
```

### Simulation test mode
1. Intellij project explorer에서 `src/main/java/edu/hanyang/kmbr/SimulationApp.java` 파일 마우스 오른쪽 버튼 클릭
2. 'Run SimulationApp.main()' 클릭. 이 과정에서 `data/points/simulation.txt` 파일이 생성됨
3. `python/visualize_simulation.py` 실행 (`cd python && python visualize_simulation.py`). 이 과정에서 `python/kmbr.gif` 파일이 생성됨

### Speed test mode
1. Intellij project explorer에서 `src/main/java/edu/hanyang/kmbr/SpeedTestApp.java` 파일 마우스 오른쪽 버튼 클릭
2. 'Run SpeedTestApp.main()' 클릭

### Cache test mode
1. Intellij project explorer에서 `src/main/java/edu/hanyang/kmbr/CacheTestApp.java` 파일 마우스 오른쪽 버튼 클릭
2. 'Run CacheTestApp.main()' 클릭

### Time test mode
1. Intellij project explorer에서 `src/main/java/edu/hanyang/kmbr/TimeTestApp.java` 파일 마우스 오른쪽 버튼 클릭
2. 'Run TimeTestApp.main()' 클릭

### Citibike test mode
1. Intellij project explorer에서 `src/main/java/edu/hanyang/kmbr/CitibikeSimulation.java` 파일 마우스 오른쪽 버튼 클릭
2. 'Run CitibikeSimulation.main()' 클릭. 결과물로 `data/citibike/` 아래에 여러개의 `.csv` 파일이 생성됨

## Reference
[1] Kumar, Abhishek et al. “GridShift: A Faster Mode-seeking Algorithm for Image Segmentation and Object Tracking.” 2022 IEEE/CVF Conference on Computer Vision and Pattern Recognition (CVPR) (2022): 8121-8129.