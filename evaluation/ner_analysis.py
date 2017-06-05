import pandas as pd


def analyze_data(data_path):
    expected, actual = collect_nes(data_path)
    labels = ['LOC', 'ORG', 'PER']
    for label in labels:
        compute_label_stats(expected, actual, label)


def collect_nes(data_path):
    data = pd.read_csv(data_path)
    data['expectedEntity'] = tags_to_entities(data['expectedTag'])
    data['actualEntity'] = tags_to_entities(data['actualTag'])
    data['tokenIndex'] = range(len(data))
    data['expectedBlock'] = compute_blocks(data, 'expectedEntity')
    data['actualBlock'] = compute_blocks(data, 'actualEntity')
    expected = pd.DataFrame({'expectedSpans': data.reset_index().groupby(
        ['expectedBlock', 'expectedEntity']).apply(min_max)}).reset_index()
    expected = expected[expected['expectedEntity'] != 'O']
    actual = pd.DataFrame({'actualSpans': data.reset_index().groupby(
        ['actualBlock', 'actualEntity']).apply(min_max)}).reset_index()
    actual = actual[actual['actualEntity'] != 'O']
    return expected, actual


def tags_to_entities(series):
    return [x[-3:] if x.endswith('-LOC') or x.endswith('-ORG') or x.endswith('-PER')
            else 'O' for x in series]


def compute_blocks(data, column_name):
    return (data[column_name].shift(
        1) != data[column_name]).astype(int).cumsum()


def min_max(data):
    return (min(data['index']), max(data['index']))


def compute_label_stats(expected, actual, label):
    expected_set = collect_filtered_set(
        expected, 'expectedEntity', 'expectedSpans', label)
    actual_set = collect_filtered_set(
        actual, 'actualEntity', 'actualSpans', label)

    tp = count_tp(expected_set, actual_set)
    fp = count_fp(expected_set, actual_set)
    fn = count_fn(expected_set, actual_set)

    precision = compute_precision(tp, fp)
    recall = compute_recall(tp, fn)
    f1 = compute_f1(precision, recall)

    print_results(label, precision, recall, f1)


def collect_filtered_set(df, column1, column2, value):
    filtered_set = set()
    for row in df.iterrows():
        entity = row[1][column1]
        if entity != value:
            continue
        filtered_set.add((entity, row[1][column2]))
    return filtered_set


def count_tp(expectedSet, actualSet):
    return len(expectedSet & actualSet)


def count_fp(expectedSet, actualSet):
    return len(actualSet - expectedSet)


def count_fn(expectedSet, actualSet):
    return len(expectedSet - actualSet)


def compute_precision(tp, fp):
    return tp / (tp + fp)


def compute_recall(tp, fn):
    return tp / (tp + fn)


def compute_f1(precision, recall):
    return 2 * (precision * recall) / (precision + recall)


def print_results(label, precision, recall, f1):
    print('Label:', label)
    print('Precision:', precision)
    print('Recall:', recall)
    print('F1:', f1)
    print()


if __name__ == '__main__':
    analyze_data('../data/private/GermEval2014-dev-results-n4.csv')
