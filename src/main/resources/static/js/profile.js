const ctx = document.getElementById('myChart');
init();

function init() {
    if (openness * conscientiousness * extraversion * agreeableness * neuroticism != 0) {
        var characterType_ = characterType([openness, conscientiousness, extraversion, agreeableness, neuroticism]);
        document.getElementById('characterType').innerText = characterType_;
        if (characterType_ == '평균형') {
            document.getElementById('characterTypeText').innerHTML = '<strong>평균형</strong>은 가장 많은 사람들이 속해있는 유형입니다. 이 유형의 사람들은 사람들에게 매우 우호적이며 성실하고, 사람 만나기를 즐기는 편입니다. 또한 다소 신경증적인 모습을 보이고 새로운 것에 개방적이지 않습니다.</p><p>남성보다는 여성에서 더 많은 비율을 보입니다.';
        } else if (characterType_ == '자기중심형') {
            document.getElementById('characterTypeText').innerHTML = '<strong>자기중심적 유형</strong>은 네 유형 중 가장 외향적입니다. 하지만 낮은 개방성, 성실성, 친화성을 보입니다. 이 유형의 사람들은 다소 무뚝뚝하거나 새로운 경험에 관심이 없을 수 있습니다.</p><p>나이가 들수록 남녀 모두 해당 유형의 비율이 감소합니다.';
        } else if (characterType_ == '내성형') {
            document.getElementById('characterTypeText').innerHTML = '<strong>내성적 유형</strong>의 사람들은 예민한 성격을 지니고 있을 수 있습니다. 하지만 이들은 양심적이고 사람들에게 친절하므로 좋은 사람으로 비춰지곤 합니다. 새로운 것을 별로 좋아하지 않는 편입니다.</p><p>나이와 해당 유형의 비율 간에 상관관계는 없습니다.';
        } else if (characterType_ == '롤모델형') {
            document.getElementById('characterTypeText').innerHTML = '<strong>롤모델형</strong>은 네 가지 유형 중 가장 사회에서 존경받는 유형입니다. 정서적으로 안정되어 있으며 성실하고 사람들에게 우호적입니다. 새로운 것에 가치를 부여하며 사람들과의 만남을 좋아합니다.</p><p>나이가 들수록 해당 유형의 비율은 증가합니다.';
        }

        var typeList = ['openness', 'conscientiousness', 'extraversion', 'agreeableness', 'neuroticism'];
        var testValue = [openness, conscientiousness, extraversion, agreeableness, neuroticism];
        var typeText = [['개방성이 높은 사람은 새로운 것을 배우거나 경험하려는 욕구가 강하고, 창의성도 높은 편입니다. 풍부한 상상력을 가지고 있고, 현실에서 일어나기 힘든 것에 대한 생각도 종종 합니다.',
        '개방성은 새로운 것을 받아들이는 자세를 보여 줍니다. 지적 호기심, 새로운 경험 추구 등과 관련이 있으며, 개방성이 높은 사람은 예술의 중요성을 높게 생각하며 예술 활동에 직접 참여하기도 합니다.',
        '개방성이 낮은 사람은 새로운 것보다 기존의 것에 가치를 둡니다. 문제를 맞닥뜨렸을 때 전통적인 방식으로 접근하는 경우가 많고 안전 지대에 있을 때 안정감을 얻습니다.'],
        ['성실성이 높은 사람은 목표지향적이고 신중합니다. 또한 이들은 매사 맡은 일을 책임감 있게 해내는 사람입니다. 의무와 규칙을 중요시하여 규율을 잘 준수합니다. 높은 성실성은 기업가로 성공하기에 가장 유리한 성격 자질입니다.',
        '성실성은 사려 깊은 정도, 충동 억제, 목표 지향적인 행동의 수준을 보여 줍니다. 일반적으로 성실성이 높을 수록 성취도와 도덕성이 높지만 지나치게 높은 성실성은 강박, 일 중독 등을 유발하기도 합니다.',
        '성실성이 낮은 사람은 계획을 좋아하지 않으며 중요한 일을 미루거나 때로는 작업을 완수하는 데에 실패하곤 합니다. 이들은 정해진 틀을 싫어하며 자유로운 것을 선호합니다.'],
        ['외향성이 높은 사람은 사람을 만나는 것에 두려움이 없으며, 관심 받기를 좋아합니다. 또한 주장이 강하여 타인의 행동을 내 식으로 통제하려는 성향을 보이기도 합니다. 높은 외향성은 사람을 대면할 일이 많은 판매원, 마케터, 정치인 등에 적합합니다.',
        '외향성이 높은 사람은 말이 많으며 주장이 강하고, 감정 표현이 활발합니다. 이들이 사람들 속에서 에너지를 얻는 반면 외향성이 낮은 사람은 혼자 있기를 선호하며, 사회적인 상황에서 에너지를 별로 얻지 못합니다.',
        '외향성이 낮은 사람은 내향적인 사람으로도 일컬어지며 군중 속에서 관심이 집중되는 것을 별로 좋아하지 않습니다. 이들은 혼자 있을 때 에너지를 얻습니다.'],
        ['우호성이 높은 사람은 타인과 관계를 맺는데 유리합니다. 이들은 겸손하며 타인을 존중합니다. 또한 인간의 존업성에 가치를 두고 타인을 배려하고 공감하는 것을 중요하게 생각합니다. 따라서 높은 우호성은 일반적으로 선호되는 자질입니다.',
        '우호성은 타인에 대한 신뢰, 이타심, 친절, 공감과 관련된 특성입니다. 일반적으로 사회는 다수의 높은 우호성을 가진 사람들과 소수의 낮은 우호성을 가진 사람들로 구성되어 있습니다.',
        '우호성이 낮은 사람은 타인에 대한 공감이 부족하고 남을 신뢰하지 않습니다. 사회적으로 어울리기에는 불리한 성격이지만 사기를 당할 확률은 낮으며 우호성이 높은 사람에 비해 연봉이 높은 경향을 보입니다. 다수의 높은 우호성을 가진 사람들 사이에서는 생존하기 유리한 자질입니다.'],
        ['신경성이 높은 사람은 쉽게 우울해지고 불안이 많으며 예민합니다. 감정 기복이 심하고, 때로는 타인에 대해 공격성 및 적대감을 보이기도 합니다. 예술가들의 신경성이 높은 편입니다.',
        '신경성은 우울, 슬픔, 정서적 불안정과 관련이 있습니다. 정서적 안정(ES)와 대비되는 개념으로 예민하게 경계해야 할 위험 요인이 적은 현대 사회에서는 낮은 신경성이 선호됩니다.',
        '신경성이 낮은 사람은 정서적으로 안정되어 있으며, 우울함을 잘 느끼지 않습니다. 스트레스에 강한 모습을 보이고 스스로의 삶에 만족하며 높은 참을성과 자제력을 보입니다.']];
        for (i = 0; i < 5; i++) {
            document.getElementById(typeList[i]).innerText = testValue[i] > 3.5 ? '높음' : testValue[i] >= 2.5 ? '평균' : '낮음';
            document.getElementById(typeList[i] + '-text').innerText = testValue[i] > 3.5 ? typeText[i][0] : testValue[i] >= 2.5 ? typeText[i][1] : typeText[i][2];
        }
    }
}

const COLORS = [
  '#4dc9f6',
  '#f67019',
  '#f53794',
  '#537bc4',
  '#acc236',
  '#166a8f',
  '#00a950',
  '#58595b',
  '#8549ba'
];

function color(index) {
    return COLORS[index % COLORS.length];
}

function transparentize(value, opacity) {
  var alpha = 1 - opacity;
  let r = parseInt(value.slice(1, 3), 16),
    g = parseInt(value.slice(3, 5), 16),
    b = parseInt(value.slice(5, 7), 16);

    return 'rgba(' + r + ', ' + g + ', ' + b + ', ' + alpha + ')'
}

function characterType(ocean) {
    var label = ['평균형', '자기중심형', '내성형', '롤모델형'];
    var average = [[-0.6, 0.2, 0.5, 0.2, 0.55], [-0.75, -0.5, 0.7, -0.55, -0.2], [-0.75, 0.15, -0.5, 0.1, -0.55], [0.2, 0.7, 0.5, 0.6, -0.7]];
    var point = [];
    for (let i = 0; i < 4; i++) {
        sum = 0;
        for (let j = 0; j < 5; j++) {
            sum += ((ocean[j] - 3) / 2 - average[i][j]) ** 2;
        }
        point.push(sum);
    }

    minValue = point[0];
    minIndex = 0;
    for (var i = 1; i < 4; i++) {
        if (point[i] < minValue) {
            minIndex = i;
            minValue = point[i];
        }
    }

    return label[minIndex];
}

const CHART_COLORS = {
  red: '#FF6384',
  orange: '#FF9F40',
  yellow: '#FFCD56',
  green: '#4BC0C0',
  blue: '#36A2EB',
  purple: '#9966FF',
  grey: '#C9CBCF'
};

responsiveFontSize = 16 / 550 * ctx.offsetWidth;
responsiveBorderWidth = 4 / 550 * ctx.offsetWidth;

Chart.defaults.font.size = responsiveFontSize;
new Chart(ctx, {
  type: 'radar',
  data: {
    labels: ['개방성', '성실성', '외향성', '우호성', '신경증'],
    datasets: [...(openness !== 0 ? [{
      label: '나의 평가',
      data: [openness, conscientiousness, extraversion, agreeableness, neuroticism],
      borderColor: CHART_COLORS.red,
      backgroundColor: transparentize(CHART_COLORS.red, 0.5),
      borderWidth: responsiveBorderWidth,
      fill: true
    }] : []), ...(openness_ !== 0 ? [{
      label: '친구의 평가',
      data: [openness_, conscientiousness_, extraversion_, agreeableness_, neuroticism_],
      borderColor: CHART_COLORS.green,
      backgroundColor: transparentize(CHART_COLORS.green, 0.5),
      borderWidth: responsiveBorderWidth,
      fill: true
    }] : []), {
      label: '한국인 평균',
      data: [2.9277, 2.9506, 3.0734, 3.0231, 3.0474],
      borderColor: CHART_COLORS.blue,
      backgroundColor: transparentize(CHART_COLORS.blue, 0.5),
      borderWidth: responsiveBorderWidth,
      fill: true
    }]
  },
  options: {
    responsive: false,
    scales: {
      r: {
        beginAtZero: true,
        min: 0,
        max: 5,
        ticks: {
          stepSize: 1,
        },
        pointLabels: {
          font: {
            family: 'Noto Sans KR',
            size: responsiveFontSize+2,
            weight: 'bold'
          }
        }
      }
    },
  },
});